package com.toyou.toyouandroid.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.CardModel

class CardAdapter : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var cardList: List<CardModel> = emptyList()

    fun setCards(cards : List<CardModel>){
        this.cardList = cards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapter.CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardAdapter.CardViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int = cardList.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val cardMessageTextView : TextView = itemView.findViewById(R.id.textMessage)

        fun bind(card : CardModel){
            cardMessageTextView.text = card.message
        }

    }
}