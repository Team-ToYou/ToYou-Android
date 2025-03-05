package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.ItemHomeBottomSheetBinding
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCard
import com.toyou.toyouandroid.presentation.fragment.home.HomeBottomSheetClickListener

class HomeBottomSheetAdapter(
    private val items: MutableList<YesterdayCard>,
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

        fun bind(item: YesterdayCard) {

            val homeBottomSheetCardDetailAdapter = HomeBottomSheetCardDetailAdapter()
            homeBottomSheetCardDetailAdapter.setCards(listOf(item))
            binding.cardList.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                adapter = homeBottomSheetCardDetailAdapter
            }
            binding.itemHomeBottomSheet = item


            binding.itemHomeBottomSheet = item

            binding.homeBottomSheetItem.setOnClickListener {
                listener.onDiaryCardClick(item.cardId)
            }

            binding.executePendingBindings()
        }
    }
}
