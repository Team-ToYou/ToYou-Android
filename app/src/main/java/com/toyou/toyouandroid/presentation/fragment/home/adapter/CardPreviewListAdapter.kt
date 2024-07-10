package com.toyou.toyouandroid.ui.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.PreviewCardModel
import timber.log.Timber

class CardPreviewListAdapter(private var cardList: List<PreviewCardModel>) : BaseAdapter() {

    fun setCards(newCards: List<PreviewCardModel>) {
        cardList = newCards
        notifyDataSetChanged()
        Timber.tag("카드1").d(cardList.toString())
    }

    override fun getCount(): Int {
        return cardList.size
    }

    override fun getItem(position: Int): Any {
        return cardList[position]
    }

    override fun getItemId(position: Int): Long {
       return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView
        if (view == null) view =
            LayoutInflater.from(parent?.context).inflate(R.layout.card_qa_list, parent, false)

        val questionText = view?.findViewById<TextView>(R.id.question)
        questionText!!.text = cardList[position].question
        val  answerText = view?.findViewById<TextView>(R.id.answer)
        answerText!!.text = cardList[position].answer

        return view!!
    }

}