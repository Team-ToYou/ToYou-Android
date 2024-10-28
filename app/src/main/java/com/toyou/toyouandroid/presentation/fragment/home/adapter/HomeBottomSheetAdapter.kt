package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.ItemHomeBottomSheetBinding
import com.toyou.toyouandroid.data.emotion.dto.DiaryCard
import com.toyou.toyouandroid.presentation.fragment.home.HomeBottomSheetClickListener

class HomeBottomSheetAdapter(
    private val items: MutableList<DiaryCard>,
//    private val cardDetails: List<CardDetail>,
    private val listener: HomeBottomSheetClickListener
) : RecyclerView.Adapter<HomeBottomSheetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeBottomSheetBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
//        val cardDetail = cardDetails.getOrNull(position)
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemHomeBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiaryCard) {

            val homeBottomSheetCardDetailAdapter = HomeBottomSheetCardDetailAdapter()
            binding.cardList.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                adapter = homeBottomSheetCardDetailAdapter
            }

            val nickname = item.nickname
            binding.itemHomeBottomSheet = item
            binding.friendNickname.text = nickname

            binding.itemDetail.text = if (nickname.isNotBlank()) {
                "To.$nickname"
            } else {
                "To. Unknown"
            }

//            cardDetail.let {
//                homeBottomSheetCardDetailAdapter.setEmotion(it.emotion)
//            }

            binding.homeBottomSheetItem.setOnClickListener {
                listener.onDiaryCardClick(item.cardId)
            }

            binding.executePendingBindings()
        }
    }
}
