package com.toyou.toyouandroid.presentation.fragment.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.toyou.toyouandroid.databinding.FragmentCalendarMyrecordBinding
import com.toyou.toyouandroid.utils.Dates.generateDatesForMonths
import com.toyou.toyouandroid.presentation.fragment.calendar.month.CalendarPagerAdapter
import com.toyou.toyouandroid.presentation.fragment.calendar.month.CalendarRVAdapter
import com.toyou.toyouandroid.utils.Dates
import java.util.Calendar
import java.util.Date

class CalendarMyRecordFragment : Fragment(), CalendarRVAdapter.OnDateClickListener {

    lateinit var navController: NavController
    private var _binding: FragmentCalendarMyrecordBinding? = null
    private val binding: FragmentCalendarMyrecordBinding
        get() = requireNotNull(_binding){"FragmentCalendarMyrecordBinding -> null"}

    private var calendar = Calendar.getInstance()
    private val startCalendar: Calendar = Calendar.getInstance().apply {
        time = Date()
    }
    private var monthDates = generateDatesForMonths(calendar, 12, 12)

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

        // 오늘로 돌아가기
        binding.btnTodayDate.setOnClickListener {
            calendar = Calendar.getInstance()
            startCalendar.time = calendar.time
            monthDates = generateDatesForMonths(calendar, 12, 12) // 이전 달과 다음 달을 포함
            updateCalendar()
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
        binding.yearMonthTextView.text = "${year}년 ${month + 1}월"

        val months = generateMonths(calendar)
        val pagerAdapter = CalendarPagerAdapter(months, month, this)
        binding.calendarViewPager.adapter = pagerAdapter
        binding.calendarViewPager.setCurrentItem(months.size / 2, false)
    }

    private fun generateMonths(calendar: Calendar): List<List<Date>> {

        val months = mutableListOf<List<Date>>()

        for (i in -12..12) {
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.MONTH, i)
            months.add(Dates.generateDates(cal))
        }

        return months
    }

    override fun onDateClick(date: Date) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}