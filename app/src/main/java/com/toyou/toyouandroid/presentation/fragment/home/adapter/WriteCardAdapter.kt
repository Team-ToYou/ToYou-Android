package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.R.id.limit_200
import com.toyou.toyouandroid.model.PreviewCardModel

class WriteCardAdapter : RecyclerView.Adapter<WriteCardAdapter.ViewHolder>() {

    private var cardList : List<PreviewCardModel> = emptyList()

    fun setCards(card: List<PreviewCardModel>){
        this.cardList = card
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteCardAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_long_edit, parent, false)
        return WriteCardAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val cardMessageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val fromWho: TextView = itemView.findViewById(R.id.fromWho_tv)
        private val answerEdit : EditText = itemView.findViewById(R.id.memo_et)
        private val wordCount: TextView = itemView.findViewById(limit_200)
        var message: String = ""

        init {
            answerEdit.addTextChangedListener(object : TextWatcher {
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
                    wordCount.text = "${message.length} / 200"
                }

                override fun afterTextChanged(s: Editable?) {
                    wordCount.text = "${message.length} / 200"
                }
            })
        }
        fun bind(card: PreviewCardModel) {
            cardMessageTextView.text = card.question
            fromWho.text = card.fromWho
        }


    }

}