package com.toyou.toyouandroid.presentation.fragment.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.ItemCalendarFriendStampBinding
import com.toyou.toyouandroid.presentation.fragment.record.friend.OnFriendDateClickListener
import com.toyou.toyouandroid.data.record.dto.DiaryCardPerDay
import timber.log.Timber

class CalendarAdapter(
    private var items: List<DiaryCardPerDay>,
    private val listener: OnFriendDateClickListener
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCalendarFriendStampBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<DiaryCardPerDay>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCalendarFriendStampBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(diaryCard: DiaryCardPerDay) {
            Timber.tag("CalendarAdapter").d("Binding view for ${diaryCard.nickname}, emotion: ${diaryCard.emotion}")
            binding.calendarFriendNickname.text = diaryCard.nickname
            binding.calendarStampIv.setImageResource(getImageResource(diaryCard.emotion))

            binding.root.setOnClickListener{
                Timber.tag("CalendarAdapter").d(this.layoutPosition.toString())
                listener.onFriendClick(diaryCard.cardId)
            }
        }

        private fun getImageResource(emotion: String?): Int {
            return when (emotion) {
                "HAPPY" -> R.drawable.home_stamp_option_happy
                "EXCITED" -> R.drawable.home_stamp_option_exciting
                "NORMAL" -> R.drawable.home_stamp_option_normal
                "NERVOUS" -> R.drawable.home_stamp_option_anxiety
                "ANGRY" -> R.drawable.home_stamp_option_upset
                else -> R.drawable.home_stamp_option_normal
            }
        }
    }
}
