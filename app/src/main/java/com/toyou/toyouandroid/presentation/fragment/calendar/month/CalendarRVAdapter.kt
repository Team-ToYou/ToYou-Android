package com.toyou.toyouandroid.presentation.fragment.calendar.month

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.CalendarItemBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarRVAdapter(
    private val dates: List<Date?>,
    currentMonth: Int,
    private val onDateClickListener: OnDateClickListener
) : RecyclerView.Adapter<CalendarRVAdapter.ViewHolder>() {

    private val thisMonth = currentMonth

    inner class ViewHolder(private val binding: CalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: Date) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val month = calendar.get(Calendar.MONTH)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            if (month != thisMonth) {
                binding.dayText.setTextColor(Color.LTGRAY)
            } else {
                when (dayOfWeek) {
                    Calendar.SATURDAY -> {
                        binding.dayText.setTextColor(Color.BLUE)
                    }
                    Calendar.SUNDAY -> {
                        binding.dayText.setTextColor(Color.RED)
                    }
                    else -> {
                        binding.dayText.setTextColor(Color.BLACK)
                    }
                }
            }

            binding.dayText.text = SimpleDateFormat("d", Locale.getDefault()).format(date)
            binding.root.setOnClickListener {
                Timber.tag("CalendarRVAdapter").d("Date clicked: $date")

                // 날짜 클릭했을 때 기록탭 날짜 변경로직
                onDateClickListener.onDateClick(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dates[position]!!)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    interface OnDateClickListener {
        fun onDateClick(date: Date)
    }

}