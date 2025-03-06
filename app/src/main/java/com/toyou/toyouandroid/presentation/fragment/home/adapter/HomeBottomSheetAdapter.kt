package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.ItemHomeBottomSheetBinding
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCard
import com.toyou.toyouandroid.presentation.fragment.home.HomeBottomSheetClickListener

class HomeBottomSheetAdapter(
    private val items: MutableList<YesterdayCard>,
    private val listener: HomeBottomSheetClickListener
) : RecyclerView.Adapter<HomeBottomSheetAdapter.ViewHolder>() {

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

            binding.itemTitle.text = item.cardContent.receiver

            binding.itemDetail.text = item.cardContent.date

            binding.friendNickname.text = item.cardContent.receiver

            when(item.cardContent.emotion){
                "HAPPY" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_happy)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFF7F3E3"))
                }
                "EXCITED" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_exciting)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFE0EEF6"))
                }
                "NORMAL" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_normal)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFDFE1F1"))
                }
                "NERVOUS" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_anxiety)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFD6E4D9"))

                }
                "ANGRY" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_upset)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFF4DDDD"))
                }
                //임의로 감정이 null일때..일수는 없지만
                else -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_upset)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFF4DDDD"))
                }
            }

            binding.executePendingBindings()
        }
    }
}
