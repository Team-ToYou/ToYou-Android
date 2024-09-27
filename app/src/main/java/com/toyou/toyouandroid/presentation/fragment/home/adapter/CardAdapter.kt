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
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import kotlinx.coroutines.NonCancellable.parent
import okhttp3.internal.addHeaderLenient

class CardAdapter(private val onItemClick: (Int, Boolean) -> Unit, private val cardViewModel: CardViewModel) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var cardList: List<CardModel> = emptyList()

    fun setCards(cards: List<CardModel>) {
        this.cardList = cards.filter { it.questionType == 1 }
        Log.d("CardAdapter", "setCards called with: $cards")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_card, parent, false)
        return CardViewHolder(view, onItemClick, cardViewModel)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int = cardList.size

    class CardViewHolder(itemView: View, onItemClick: (Int, Boolean) -> Unit, private val cardViewModel: CardViewModel ) :
        RecyclerView.ViewHolder(itemView) {
        private val cardMessageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val button: Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val fromWho: TextView = itemView.findViewById(R.id.fromWho_tv)
        init {
            button.setOnClickListener {
                val currentCount = cardViewModel.countSelection.value ?: 0
                if (isSelected) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                    //updateCardModelSelectionState(isSelected)
                    Log.d("선택7", isSelected.toString())

                } else if (currentCount < 5) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                    //updateCardModelSelectionState(isSelected)

                } else {
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
            }



        /*button.setOnClickListener {
            isSelected = !isSelected
            updateButtonBackground(isSelected)
            onItemClick(adapterPosition, isSelected)
        }*/

        }

        fun bind(card: CardModel) {
            cardMessageTextView.text = card.message
            fromWho.text = card.fromWho
            isSelected = card.isButtonSelected

            // 버튼 배경을 업데이트
            updateButtonBackground(isSelected)
        }


        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes =
                if (isSelected) R.drawable.create_check else R.drawable.create_uncheck
            button.setBackgroundResource(backgroundRes)
        }

    }

}
