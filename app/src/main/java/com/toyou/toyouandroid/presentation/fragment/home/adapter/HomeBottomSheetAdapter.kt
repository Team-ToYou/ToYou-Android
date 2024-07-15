package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.ItemHomeBottomSheetBinding
import com.toyou.toyouandroid.model.HomeBottomSheetItem

class HomeBottomSheetAdapter(private val items: List<HomeBottomSheetItem>) : RecyclerView.Adapter<HomeBottomSheetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeBottomSheetBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemHomeBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeBottomSheetItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}
