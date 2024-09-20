package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.ItemHomeBottomSheetBinding
import com.toyou.toyouandroid.model.HomeBottomSheetItem
import com.toyou.toyouandroid.presentation.fragment.emotionstamp.network.DiaryCard

class HomeBottomSheetAdapter : RecyclerView.Adapter<HomeBottomSheetAdapter.ViewHolder>() {

    private val diaryCards = mutableListOf<DiaryCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeBottomSheetBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = diaryCards[position]
        holder.bind(item)
    }

    fun submitList(cards: List<DiaryCard>) {
        diaryCards.clear()
        diaryCards.addAll(cards)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return diaryCards.size
    }

    inner class ViewHolder(private val binding: ItemHomeBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(diaryCard: DiaryCard) {
//            binding.item = diaryCard
            binding.friendNickname.text = diaryCard.nickname
            binding.executePendingBindings()
        }
    }
}
