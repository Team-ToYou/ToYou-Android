package com.toyou.toyouandroid.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.PreviewCardModel

class CardPreviewListAdapter(val context: Context, val cardList: ArrayList<PreviewCardModel>) : BaseAdapter(){
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
        val view : View = LayoutInflater.from(context).inflate(R.layout.card_qa_list, null)
        val questionText = view?.findViewById<TextView>(R.id.question)
        questionText!!.text = cardList[position].question
        val  answerText = view?.findViewById<TextView>(R.id.answer)
        answerText!!.text = cardList[position].answer

        return view
    }

}