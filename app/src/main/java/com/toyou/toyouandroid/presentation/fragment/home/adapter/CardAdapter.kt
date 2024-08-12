package com.toyou.toyouandroid.presentation.fragment.home.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.CardModel

class CardAdapter(private val onItemClick: (Int, Boolean) -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var cardList: List<CardModel> = emptyList()

    fun setCards(cards: List<CardModel>) {
        this.cardList = cards.filter { it.questionType == 2 }
        Log.d("CardAdapter", "setCards called with: $cards")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_card, parent, false)
        return CardViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int = cardList.size

    class CardViewHolder(itemView: View, onItemClick: (Int, Boolean) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val cardMessageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val button: Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val fromWho: TextView = itemView.findViewById(R.id.fromWho_tv)
        init {
            button.setOnClickListener {
                isSelected = !isSelected
                updateButtonBackground(isSelected)
                onItemClick(adapterPosition, isSelected)
            }

        }

        fun bind(card: CardModel) {
            cardMessageTextView.text = card.message
            fromWho.text = card.fromWho
        }


        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes =
                if (isSelected) R.drawable.create_check else R.drawable.create_uncheck
            button.setBackgroundResource(backgroundRes)
        }

    }

}
