package com.toyou.toyouandroid.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.CardModel

class CardAdapter(private val onItemClick: (Int, Boolean) -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var cardList: List<CardModel> = emptyList()

    fun setCards(cards : List<CardModel>){
        this.cardList = cards
        Log.d("CardAdapter", "setCards called with: $cards")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_card, parent, false)
        return CardViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: CardAdapter.CardViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int = cardList.size

    class CardViewHolder(itemView: View, onItemClick: (Int, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView){
        private val cardMessageTextView : TextView = itemView.findViewById(R.id.textMessage)
        private val button : Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val editText : EditText = itemView.findViewById(R.id.memo_et)


        init {
            button.setOnClickListener {
                Log.d("선택1", "$isSelected")
                isSelected = !isSelected
                //버튼 클릭 업데이트 후 onItemClick 함수 호출하기!
                updateButtonBackground(isSelected)
                updateEditTextBoxVisibility(isSelected)
                onItemClick(adapterPosition, isSelected)
                Log.d("선택2", "$isSelected")
            }
        }


        fun bind(card : CardModel){
            cardMessageTextView.text = card.message
            //card.isButtonSelected = isSelected
        }
        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes = if (isSelected) R.drawable.create_unclicked_btn else R.drawable.create_clicked_btn
            button.setBackgroundResource(backgroundRes)
        }

        private fun updateEditTextBoxVisibility(isSelected: Boolean) {
            if (isSelected) {
                editText.visibility = View.VISIBLE
            } else{
                editText.visibility = View.GONE
            }
        }

    }
}