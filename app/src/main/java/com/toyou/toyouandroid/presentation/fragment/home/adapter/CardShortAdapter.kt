package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.CardShortModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel

class CardShortAdapter (private val onItemClick: (Int, Boolean) -> Unit, private val cardViewModel: CardViewModel) : RecyclerView.Adapter<CardShortAdapter.CardViewHolder>() {
    private var cardList: List<CardShortModel> = emptyList()

    fun setCards(cards: List<CardShortModel>) {
        this.cardList = cards.filter { it.questionType == 0 }
        Log.d("CardAdapter", "setCards called with: $cards")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_short_card, parent, false)
        return CardViewHolder(view, onItemClick, cardViewModel)
    }

    override fun onBindViewHolder(holder: CardShortAdapter.CardViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int = cardList.size

    class CardViewHolder(itemView: View, onItemClick: (Int, Boolean) -> Unit, private val cardViewModel: CardViewModel) :
        RecyclerView.ViewHolder(itemView) {
        private val cardMessageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val button: Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val fromWho: TextView = itemView.findViewById(R.id.fromWho_tv)

        init {
            // 기본적으로 클릭 리스너는 항상 동작하도록 설정
            button.setOnClickListener {
                val currentCount = cardViewModel.countSelection.value ?: 0
                if (isSelected) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                } else if (currentCount < 5) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                } else{
                    Toast.makeText(itemView.context, "질문은 최대 5개까지 선택할 수 있습니다", Toast.LENGTH_SHORT).show()
                }
            }

            // LiveData 변화 관찰
            val lifecycleOwner = itemView.findViewTreeLifecycleOwner() // LifecycleOwner 가져옴
            lifecycleOwner?.let {
                cardViewModel.countSelection.observe(it, Observer { count ->
                    // 필요한 경우 UI 업데이트 등 처리
                })
            } ?: run {
                Log.e("CardViewHolder", "LifecycleOwner is null")
            }

        }

        fun bind(card: CardShortModel) {
            cardMessageTextView.text = card.message
            fromWho.text = "From. ${card.fromWho}"
            isSelected = card.isButtonSelected
            updateButtonBackground(isSelected)
        }


        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes =
                if (isSelected) R.drawable.create_check else R.drawable.create_uncheck
            button.setBackgroundResource(backgroundRes)
        }

    }

}
