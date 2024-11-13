package com.toyou.toyouandroid.presentation.fragment.record.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCalendarMyrecordBinding
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.data.record.dto.DiaryCard
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.model.calendar.MyDate
import com.toyou.toyouandroid.presentation.fragment.record.RecordViewModelFactory
import com.toyou.toyouandroid.utils.calendar.MyDates
import timber.log.Timber
import java.util.Calendar
import java.util.Date

class CalendarMyRecordFragment : Fragment(), OnMyDateClickListener {

    lateinit var navController: NavController
    private var _binding: FragmentCalendarMyrecordBinding? = null
    private val binding: FragmentCalendarMyrecordBinding
        get() = requireNotNull(_binding){"FragmentCalendarMyrecordBinding -> null"}
    private val myRecordViewModel: MyRecordViewModel by activityViewModels {
        RecordViewModelFactory(RecordRepository(AuthNetworkModule.getClient().create(RecordService::class.java)))
    }

    private var calendar = Calendar.getInstance()
    private val startCalendar: Calendar = Calendar.getInstance().apply {
        time = Date()
    }
    private var currentYear: Int = -1
    private var currentMonth: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalendarMyrecordBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        startCalendar.time = calendar.time

        // 월 달력
        binding.calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.calendarViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 12 + position)
                updateCalendar()
            }
        })

        binding.calendarLeftBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        binding.calendarRightBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        // 나의 기록 api 호출 후 정보 재가공
        myRecordViewModel.diaryCards.observe(viewLifecycleOwner) { diaryCards ->
            Timber.tag("CalendarFragment").d("DiaryCards loaded: ${diaryCards.size} items")
            Timber.tag("CalendarFragment").d("DiaryCards loaded: $diaryCards")

            val imageMap = mapDiaryCardsToImages(diaryCards)
            updateCalendarWithImages(imageMap)
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
            Timber.tag("CalendarFragment").d("Updating calendar for: ${year}년 ${month + 1}월")

            myRecordViewModel.loadDiaryCards(year, month + 1) // userId 대체
            Timber.tag("CalendarFragment").d("loadDiaryCards called for User: 1, Year: $year, Month: ${month + 1}")
        }
    }

    private fun mapDiaryCardsToImages(diaryCards: List<DiaryCard>): Map<Pair<Int, Int>, Map<Int, Pair<Int, Int?>>> {
        Timber.tag("CalendarFragment").d("Mapping diary cards to images")

        val imageMap = mutableMapOf<Pair<Int, Int>, MutableMap<Int, Pair<Int, Int?>>>()

        diaryCards.forEach { diaryCard ->
            val dateParts = diaryCard.date.split("-")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1 // Calendar.MONTH는 0부터 시작
            val day = dateParts[2].toInt()
            Timber.tag("CalendarFragment").d("$month")

            val emotionImageResId = when (diaryCard.emotion) {
                "HAPPY" -> R.drawable.home_stamp_option_happy
                "EXCITED" -> R.drawable.home_stamp_option_exciting
                "NORMAL" -> R.drawable.home_stamp_option_normal
                "NERVOUS" -> R.drawable.home_stamp_option_anxiety
                "ANGRY" -> R.drawable.home_stamp_option_upset
                else -> null
            }

            if (emotionImageResId != null) {
                val monthMap = imageMap.getOrPut(Pair(year, month)) { mutableMapOf() }
                monthMap[day] = Pair(emotionImageResId, diaryCard.cardId)
            }
        }

        Timber.tag("CalendarFragment").d("Image mapping complete: $imageMap")

        return imageMap
    }

    private fun updateCalendarWithImages(imageMap: Map<Pair<Int, Int>, Map<Int, Pair<Int, Int?>>>) {
        Timber.tag("CalendarFragment").d("Updating calendar with images")

        binding.yearMonthTextView.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        val months = generateMonths(calendar, imageMap)
        val pagerAdapter = MyrecordCalendarPagerAdapter(months, calendar.get(Calendar.MONTH), this)
        binding.calendarViewPager.offscreenPageLimit = 3
        binding.calendarViewPager.adapter = pagerAdapter
        binding.calendarViewPager.setCurrentItem(months.size / 2, false)

        Timber.tag("CalendarFragment").d("Calendar update with images complete")
    }

    private fun generateMonths(calendar: Calendar, imageMap: Map<Pair<Int, Int>, Map<Int, Pair<Int, Int?>>>): List<List<MyDate>> {

        val months = mutableListOf<List<MyDate>>()

        for (i in -12..12) {
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.MONTH, i)
            months.add(MyDates.generateDates(cal, imageMap))
        }

        return months
    }

    override fun onDateClick(date: Date, cardId: Int?) {
        Timber.tag("CalendarFragment").d("$date $cardId")
        cardId?.let {
            val bundle = Bundle().apply {
                putInt("cardId", it)
                putString("date", date.toString())
            }
            navController.navigate(R.id.action_navigation_record_to_my_card_container_fragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}