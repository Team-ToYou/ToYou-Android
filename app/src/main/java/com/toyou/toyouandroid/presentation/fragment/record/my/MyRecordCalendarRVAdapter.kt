package com.toyou.toyouandroid.presentation.fragment.record.my

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.CalendarMyrecordItemBinding
import com.toyou.toyouandroid.model.MyDate
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyRecordCalendarRVAdapter(
    private val dates: List<MyDate>,
    currentMonth: Int,
    private val onDateClickListener: OnMyDateClickListener
) : RecyclerView.Adapter<MyRecordCalendarRVAdapter.ViewHolder>() {

    private val thisMonth = currentMonth

    inner class ViewHolder(private val binding: CalendarMyrecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(myDate: MyDate) {
            val date = myDate.date
            val cardId = myDate.cardId
            val imageResId = myDate.imageResId
            val calendar = Calendar.getInstance()
            calendar.time = date
            val month = calendar.get(Calendar.MONTH)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            if (month != thisMonth) {
                binding.dayText.setTextColor(Color.LTGRAY)
            } else {
                when (dayOfWeek) {
                    Calendar.SATURDAY -> {
                        binding.dayText.setTextColor(Color.BLACK)
                    }
                    Calendar.SUNDAY -> {
                        binding.dayText.setTextColor(Color.RED)
                    }
                    else -> {
                        binding.dayText.setTextColor(Color.BLACK)
                    }
                }
            }

            // 이미지 설정
            if (imageResId != null) {
                binding.myrecordImageView.setImageResource(imageResId)
                binding.myrecordImageView.visibility = View.VISIBLE
            } else {
                binding.myrecordImageView.visibility = View.GONE
            }

            binding.dayText.text = SimpleDateFormat("d", Locale.getDefault()).format(date)
            binding.root.setOnClickListener {
                Timber.tag("CalendarRVAdapter").d("Date clicked: $date")

                // 날짜 클릭했을 때 기록탭 날짜 변경로직
                onDateClickListener.onDateClick(date, cardId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarMyrecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount(): Int {
        return dates.size
    }
}