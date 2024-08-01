package com.toyou.toyouandroid.presentation.fragment.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.ItemDayoftheweekBinding

class DayOfTheWeekAdapter(private val days: List<String>) : RecyclerView.Adapter<DayOfTheWeekAdapter.DayViewHolder>() {

    class DayViewHolder(private val binding: ItemDayoftheweekBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String) {
            binding.dayTextOfWeek.text = day
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayoftheweekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size

}