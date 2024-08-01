package com.toyou.toyouandroid.presentation.fragment.record.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCalendarFriendrecordBinding
import com.toyou.toyouandroid.model.FriendDate
import com.toyou.toyouandroid.presentation.fragment.record.CalendarAdapter
import com.toyou.toyouandroid.presentation.fragment.record.CalendarItemDecoration
import com.toyou.toyouandroid.utils.FriendDates
import com.toyou.toyouandroid.utils.FriendDates.generateFriendDatesForMonths
import com.toyou.toyouandroid.utils.FriendRecordData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFriendRecordFragment : Fragment(), FriendCalendarRVAdapter.OnDateClickListener {

    lateinit var navController: NavController
    private var _binding: FragmentCalendarFriendrecordBinding? = null
    private val binding: FragmentCalendarFriendrecordBinding
        get() = requireNotNull(_binding){"FragmentCalendarFriendrecordBinding -> null"}

    private var calendar = Calendar.getInstance()
    private val startCalendar: Calendar = Calendar.getInstance().apply {
        time = Date()
    }
    private var monthDates = generateFriendDatesForMonths(calendar, 12, 12)

    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalendarFriendrecordBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        startCalendar.time = calendar.time

        val today = Calendar.getInstance().time
        val formattedToday = SimpleDateFormat("yyyyMMdd 나의 기록", Locale.getDefault()).format(today)
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

        // 오늘로 돌아가기
        binding.btnTodayDate.setOnClickListener {
            calendar = Calendar.getInstance()
            startCalendar.time = calendar.time
            monthDates = generateFriendDatesForMonths(calendar, 12, 12) // 이전 달과 다음 달을 포함
            updateCalendar()
            onDateClick(today)
        }

        updateCalendar() // 초기 달력 업데이트
        dayTextView()

        // 오늘 날짜에 해당하는 아이템 가져오기
        val items = FriendRecordData.getItemsForDate(SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(today))

        calendarAdapter = CalendarAdapter(items)
        binding.calendarRv.layoutManager = GridLayoutManager(context, 5)
        binding.calendarRv.adapter = calendarAdapter

        val verticalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        val horizontalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing_side)
        binding.calendarRv.addItemDecoration(CalendarItemDecoration(horizontalSpaceHeight, verticalSpaceHeight))
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
        binding.yearMonthTextView.text = "${year}년 ${month + 1}월"

        val months = generateMonths(calendar)
        val pagerAdapter = FriendCalendarPagerAdapter(months, month, this)
        binding.calendarViewPager.adapter = pagerAdapter
        binding.calendarViewPager.setCurrentItem(months.size / 2, false)
    }

    private fun generateMonths(calendar: Calendar): List<List<FriendDate>> {

        val months = mutableListOf<List<FriendDate>>()

        for (i in -12..12) {
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.MONTH, i)
            months.add(FriendDates.generateFriendDates(cal))
        }

        return months
    }

    override fun onDateClick(date: Date) {
        // 여기서 record_title의 텍스트를 클릭한 날짜로 설정할 수 있습니다.
        val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
        val items = FriendRecordData.getItemsForDate(formattedDate)
        calendarAdapter.updateData(items)
        binding.recordTitle.text = SimpleDateFormat("yyyyMMdd 친구 기록", Locale.getDefault()).format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}