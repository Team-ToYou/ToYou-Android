package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.text.Editable
import android.text.TextWatcher
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

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int = cardList.size

    class CardViewHolder(itemView: View, onItemClick: (Int, Boolean,) -> Unit) : RecyclerView.ViewHolder(itemView){
        private val cardMessageTextView : TextView = itemView.findViewById(R.id.textMessage)
        private val button : Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val editText : EditText = itemView.findViewById(R.id.memo_et)
        private val fromWho : TextView = itemView.findViewById(R.id.fromWho_tv)
        private val fromWhoEt : TextView = itemView.findViewById(R.id.fromWhoEt_tv)

        var message : String = ""


        init {
            button.setOnClickListener {
                isSelected = !isSelected
                //버튼 클릭 업데이트 후 onItemClick 함수 호출하기!
                updateButtonBackground(isSelected)
                updateEditTextBoxVisibility(isSelected)
                updateUiEditTextVisibility(isSelected)
                onItemClick(adapterPosition, isSelected,)


            editText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    message = s.toString()

                }

                override fun afterTextChanged(s: Editable?) {

                }

            })


            }
        }

        fun bind(card : CardModel){
            cardMessageTextView.text = card.message
            if (isSelected) {
                fromWhoEt.text = card.fromWho
            } else{
                fromWho.text = card.fromWho
            }
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

        private fun updateUiEditTextVisibility(isSelected: Boolean){
            if (isSelected){
                fromWho.visibility = View.GONE
                fromWhoEt.visibility = View.VISIBLE
            } else{
                fromWho.visibility = View.VISIBLE
                fromWhoEt.visibility = View.GONE
            }
        }

    }
}