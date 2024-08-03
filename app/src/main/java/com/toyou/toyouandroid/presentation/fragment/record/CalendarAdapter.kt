package com.toyou.toyouandroid.presentation.fragment.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.ItemCalendarFriendStampBinding
import com.toyou.toyouandroid.model.CalendarItem

class CalendarAdapter(private var items: List<CalendarItem>) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

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

    fun updateData(newItems: List<CalendarItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCalendarFriendStampBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}
