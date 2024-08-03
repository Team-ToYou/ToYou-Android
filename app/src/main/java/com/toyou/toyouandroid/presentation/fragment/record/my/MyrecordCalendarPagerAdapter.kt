package com.toyou.toyouandroid.presentation.fragment.record.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.CalendarPageBinding
import com.toyou.toyouandroid.model.MyDate
import com.toyou.toyouandroid.presentation.fragment.record.DayOfTheWeekAdapter

class MyrecordCalendarPagerAdapter(
    private val datesList: List<List<MyDate>>,
    private val currentMonth: Int,
    private val onDateClickListener: MyRecordCalendarRVAdapter.OnDateClickListener
) : RecyclerView.Adapter<MyrecordCalendarPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CalendarPageBinding) : RecyclerView.ViewHolder(binding.root) {
        val calendarRecyclerView: RecyclerView = binding.calendarViewPager
        val dayOfTheWeekRecyclerView: RecyclerView = binding.dayOfTheWeekRecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 각 페이지의 RecyclerView에 CalendarAdapter를 설정하고, 해당 월의 날짜 데이터를 연결
    // GridLayoutManager를 사용하여 달력을 그리드 형식으로 표시
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dates = datesList[position]
        val adapter = MyRecordCalendarRVAdapter(dates, currentMonth, onDateClickListener)

        holder.calendarRecyclerView.adapter = adapter
        holder.calendarRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 7)

        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val dayOfWeekAdapter = DayOfTheWeekAdapter(daysOfWeek)
        holder.dayOfTheWeekRecyclerView.adapter = dayOfWeekAdapter
        holder.dayOfTheWeekRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 7)
    }

    override fun getItemCount(): Int {
        return datesList.size
    }
}
