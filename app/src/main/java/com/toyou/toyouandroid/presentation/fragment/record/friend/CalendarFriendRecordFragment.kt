package com.toyou.toyouandroid.presentation.fragment.record.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCalendarFriendrecordBinding
import com.toyou.toyouandroid.presentation.fragment.record.CalendarAdapter
import com.toyou.toyouandroid.presentation.fragment.record.CalendarItemDecoration
import com.toyou.toyouandroid.data.record.dto.DiaryCardNum
import com.toyou.toyouandroid.model.calendar.FriendDate
import com.toyou.toyouandroid.utils.calendar.FriendDates
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CalendarFriendRecordFragment : Fragment(), OnFriendDateClickListener {

    lateinit var navController: NavController
    private var _binding: FragmentCalendarFriendrecordBinding? = null
    private val binding: FragmentCalendarFriendrecordBinding
        get() = requireNotNull(_binding){"FragmentCalendarFriendrecordBinding -> null"}

    private var calendar = Calendar.getInstance()
    private val startCalendar: Calendar = Calendar.getInstance().apply {
        time = Date()
    }

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var listener: OnFriendDateClickListener

    private var currentYear: Int = -1
    private var currentMonth: Int = -1

    private val friendRecordViewModel: FriendRecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarFriendrecordBinding.inflate(inflater, container, false)

        listener = object : OnFriendDateClickListener {
            override fun onDateClick(date: Date) {
                val calendar = Calendar.getInstance()
                calendar.time = date

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val formattedDate = SimpleDateFormat("yyyyMMdd 친구 기록", Locale.getDefault()).format(date)
                binding.recordTitle.text = formattedDate

                friendRecordViewModel.loadDiaryCardPerDay(year, month, day) // userId 대체
            }

            override fun onFriendClick(cardId: Int?) {
                Timber.tag("CalendarFriendRecordFragment").d("$cardId")
                cardId?.let {
                    val bundle = Bundle().apply {
                        putInt("cardId", it)
                    }
                    navController.navigate(R.id.action_navigation_record_to_friend_card_container_fragment, bundle)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        startCalendar.time = calendar.time

        val today = Calendar.getInstance().time
        val formattedToday = SimpleDateFormat("yyyyMMdd 친구 기록", Locale.getDefault()).format(today)
        binding.recordTitle.text = formattedToday

        // 월 달력
        binding.calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.calendarViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 12 + position)
                updateCalendar()
            }
        })

        binding.calendarArrowLeft.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        binding.calendarArrowRight.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        calendarAdapter = CalendarAdapter(emptyList(), listener)

        binding.calendarRv.layoutManager = GridLayoutManager(context, 5)
        binding.calendarRv.adapter = calendarAdapter

        val verticalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        val horizontalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing_side)
        binding.calendarRv.addItemDecoration(CalendarItemDecoration(horizontalSpaceHeight, verticalSpaceHeight))

        friendRecordViewModel.diaryCardsNum.observe(viewLifecycleOwner) { diaryCardsNum ->
            Timber.tag("CalendarFriendRecordFragment").d("DiaryCards loaded: ${diaryCardsNum.size} items")
            Timber.tag("CalendarFriendRecordFragment").d("DiaryCards loaded: $diaryCardsNum")

            val cardNumMap = mapDiaryCardsToCardNum(diaryCardsNum)
            updateCalendarWithCardNum(cardNumMap)
        }

        friendRecordViewModel.diaryCardPerDay.observe(viewLifecycleOwner) { diaryCardPerDay ->
            calendarAdapter.updateData(diaryCardPerDay)
        }

        updateCalendar() // 초기 달력 업데이트
        dayTextView()
    }

    private fun dayTextView() {

        val startCalendarClone = startCalendar.clone() as Calendar // 복제된 객체 생성
        val endCalendar = startCalendarClone.clone() as Calendar
        endCalendar.add(Calendar.DATE, 6)

        // 일요일까지의 날짜 차이를 계산
        val differenceToSunday =
            (Calendar.SUNDAY + 7) - startCalendarClone.get(Calendar.DAY_OF_WEEK)
        val endSunDayCalendar = startCalendarClone.clone() as Calendar
        endSunDayCalendar.add(Calendar.DATE, differenceToSunday)
    }

    private fun updateCalendar() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        // API 호출은 년, 월이 바뀔 때만 수행
        if (year != currentYear || month != currentMonth) {
            currentYear = year
            currentMonth = month

            binding.yearMonthTextView.text = "${year}년 ${month + 1}월"
            Timber.tag("CalendarFriendRecordFragment").d("Updating calendar for: ${year}년 ${month + 1}월")

            friendRecordViewModel.loadDiaryCardsNum(year, month + 1) // userId 대체
            Timber.tag("CalendarFriendRecordFragment").d("loadDiaryCards called for User: 1, Year: $year, Month: ${month + 1}")
        }
    }

    private fun mapDiaryCardsToCardNum(diaryCardsNum: List<DiaryCardNum>): Map<Pair<Int, Int>, Map<Int, Int>> {
        Timber.tag("CalendarFriendRecordFragment").d("Mapping diary cards to images")

        val cardNumMap = mutableMapOf<Pair<Int, Int>, MutableMap<Int, Int>>()

        diaryCardsNum.forEach { diaryCardNum ->
            val dateParts = diaryCardNum.date.split("-")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1 // Calendar.MONTH는 0부터 시작
            val day = dateParts[2].toInt()
            Timber.tag("CalendarFriendRecordFragment").d("$month")

            val cardNum = diaryCardNum.cardNum

            val monthMap = cardNumMap.getOrPut(Pair(year, month)) { mutableMapOf() }
            monthMap[day] = cardNum
        }

        Timber.tag("CalendarFriendRecordFragment").d("Image mapping complete: $cardNumMap")

        return cardNumMap
    }

    private fun updateCalendarWithCardNum(cardNumMap: Map<Pair<Int, Int>, Map<Int, Int>>) {
        Timber.tag("CalendarFriendRecordFragment").d("Updating calendar with images")

        binding.yearMonthTextView.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        val months = generateMonths(calendar, cardNumMap)
        val pagerAdapter = FriendCalendarPagerAdapter(months, calendar.get(Calendar.MONTH), this)
        binding.calendarViewPager.offscreenPageLimit = 3
        binding.calendarViewPager.adapter = pagerAdapter
        binding.calendarViewPager.setCurrentItem(months.size / 2, false)

        Timber.tag("CalendarFriendRecordFragment").d("Calendar update with images complete")
    }

    private fun generateMonths(calendar: Calendar, cardNumMap: Map<Pair<Int, Int>, Map<Int, Int>>): List<List<FriendDate>> {

        val months = mutableListOf<List<FriendDate>>()

        for (i in -12..12) {
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.MONTH, i)
            months.add(FriendDates.generateFriendDates(cal, cardNumMap))
        }

        return months
    }

    override fun onDateClick(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val formattedDate = SimpleDateFormat("yyyyMMdd 친구 기록", Locale.getDefault()).format(date)
        binding.recordTitle.text = formattedDate

        friendRecordViewModel.loadDiaryCardPerDay(year, month, day) // userId 대체
    }

    override fun onFriendClick(cardId: Int?) {
        Timber.tag("CalendarFriendRecordFragment").d("$cardId")
        cardId?.let {
            val bundle = Bundle().apply {
                putInt("cardId", it)
            }
            navController.navigate(R.id.action_navigation_record_to_friend_card_container_fragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}