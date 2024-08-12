package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel

class ShortCardAdapter(private val cardViewModel: CardViewModel) : RecyclerView.Adapter<ShortCardAdapter.ViewHolder>() {

    private var cardList : List<PreviewCardModel> = emptyList()

    fun setCards(cards: List<PreviewCardModel>) {
        this.cardList = cards.filter { it.type == 0 }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortCardAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_short_edit, parent, false)
        return ShortCardAdapter.ViewHolder(view, cardViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    class ViewHolder(itemView : View, private val cardViewModel: CardViewModel) : RecyclerView.ViewHolder(itemView){
        private val cardMessageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val fromWho: TextView = itemView.findViewById(R.id.fromWho_tv)
        private val answerEdit : EditText = itemView.findViewById(R.id.memo_et)
        private val wordCount: TextView = itemView.findViewById(R.id.limit_200)
        var message: String = ""

        init {
            answerEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    wordCount.text = "(0/50)"
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    message = s.toString()
                    wordCount.text = "${message.length} / 50"
                    cardViewModel.setEditTextFilled(message.isNotEmpty())
                   // cardViewModel.updateCardAnswer(adapterPosition, message)

                }

                override fun afterTextChanged(s: Editable?) {
                    wordCount.text = "${message.length} / 50"
                }
            })
        }
        fun bind(card: PreviewCardModel) {
            cardMessageTextView.text = card.question
            fromWho.text = card.fromWho
            answerEdit.setText(card.answer)

        }

    }


}