package com.toyou.toyouandroid.presentation.fragment.record.friend

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.CalendarFriendrecordItemBinding
import com.toyou.toyouandroid.model.calendar.FriendDate
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FriendCalendarRVAdapter(
    private val dates: List<FriendDate>,
    currentMonth: Int,
    private val onDateClickListener: OnFriendDateClickListener
) : RecyclerView.Adapter<FriendCalendarRVAdapter.ViewHolder>() {

    private val thisMonth = currentMonth

    inner class ViewHolder(private val binding: CalendarFriendrecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friendDate: FriendDate) {
            val date = friendDate.date
            val people = friendDate.people
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
            if (people == null) {
                binding.friendrecordImageView.visibility = View.GONE
                binding.friendrecordPeople.visibility = View.GONE
            } else if (people == "null") {
                binding.friendrecordImageView.visibility = View.GONE
                binding.friendrecordPeople.visibility = View.GONE
            } else {
                binding.friendrecordPeople.text = people
                binding.friendrecordImageView.visibility = View.VISIBLE
                binding.friendrecordPeople.visibility = View.VISIBLE
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
        val binding = CalendarFriendrecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount(): Int {
        return dates.size
    }
}