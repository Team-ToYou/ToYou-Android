package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
        private val wordCount : TextView = itemView.findViewById(R.id.limit_200)
        private val chooseTwo : LinearLayout = itemView.findViewById(R.id.choose_two_linear)
        private val chooseThree : LinearLayout = itemView.findViewById(R.id.choose_three_linear)
        private val chooseFour : LinearLayout = itemView.findViewById(R.id.choose_forth_linear)
        private val chooseFive : LinearLayout = itemView.findViewById(R.id.choose_five_linear)

        var message : String = ""


        init {
            button.setOnClickListener {
                isSelected = !isSelected
                //버튼 클릭 업데이트 후 onItemClick 함수 호출하기!
                updateButtonBackground(isSelected)
                updateUiEditTextVisibility(isSelected)
                onItemClick(adapterPosition, isSelected,)


            editText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    wordCount.text = "(0/200)"
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    message = s.toString()
                    wordCount.text = message.length.toString() + "( / 200)"
                }

                override fun afterTextChanged(s: Editable?) {
                    wordCount.text = message.length.toString() + "( / 200)"
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

            when(card.questionType){
                1 -> updateEditTextBoxVisibility(isSelected)
                2 -> updateTwoVisibility(isSelected)
                3 -> updateThreeVisibility(isSelected)
                4 -> updateFourVisibility(isSelected)
                5 -> updateFiveVisibility(isSelected)

            }
        }
        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes = if (isSelected) R.drawable.create_unclicked_btn else R.drawable.create_clicked_btn
            button.setBackgroundResource(backgroundRes)
        }

        private fun updateEditTextBoxVisibility(isSelected: Boolean) {
            if (isSelected) {
                editText.visibility = View.VISIBLE
                wordCount.visibility = View.VISIBLE
            } else{
                editText.visibility = View.GONE
                wordCount.visibility = View.GONE

            }
        }

        private fun updateTwoVisibility(isSelected: Boolean) {
            if (isSelected) {
                chooseTwo.visibility = View.VISIBLE
            } else{
                chooseTwo.visibility = View.GONE
            }
        }

        private fun updateThreeVisibility(isSelected: Boolean) {
            if (isSelected) {
                chooseThree.visibility = View.VISIBLE
            } else{
                chooseThree.visibility = View.GONE
            }
        }

        private fun updateFourVisibility(isSelected: Boolean) {
            if (isSelected) {
                chooseFour.visibility = View.VISIBLE
            } else{
                chooseFour.visibility = View.GONE
            }
        }

        private fun updateFiveVisibility(isSelected: Boolean) {
            if (isSelected) {
                chooseFive.visibility = View.VISIBLE
            } else{
                chooseFive.visibility = View.GONE
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