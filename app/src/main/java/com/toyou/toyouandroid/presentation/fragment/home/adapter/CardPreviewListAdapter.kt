package com.toyou.toyouandroid.ui.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.PreviewChooseModel
import com.toyou.toyouandroid.model.multi_type1
import com.toyou.toyouandroid.model.type1
import com.toyou.toyouandroid.model.type2
import com.toyou.toyouandroid.model.type3
import com.toyou.toyouandroid.presentation.fragment.home.adapter.ChooseCardAdapter
import timber.log.Timber

class CardPreviewListAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cardList : List<PreviewCardModel> = emptyList()

    fun setCards(newCards: List<PreviewCardModel>) {
        cardList = newCards
        notifyDataSetChanged()
        Timber.tag("카드1").d(cardList.toString())
    }

    override fun getItemViewType(position: Int): Int {
        return cardList[position].type
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            type1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.card_qa_list,
                    parent,
                    false
                )
                MultiViewHolderPreview1(view)
            }
            type2 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.card_qa_list,
                    parent,
                    false
                )
                MultiViewHolderPreview1(view)
            }
            type3 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_answer_option_two,
                    parent,
                    false
                )
                MultiViewHolderPreview3(view)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_answer_option_three,
                    parent,
                    false
                )
                MultiViewHolderPreview4(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (cardList[position].type) {
            type1 -> {
                (holder as MultiViewHolderPreview1).bind(cardList[position])
                holder.setIsRecyclable(false)
            }
            type2 -> {
                (holder as MultiViewHolderPreview1).bind(cardList[position])
                holder.setIsRecyclable(false)
            }
            type3 -> {
                (holder as CardPreviewListAdapter.MultiViewHolderPreview3).bind(cardList[position])
                holder.setIsRecyclable(false)
            }

            else -> {
                (holder as MultiViewHolderPreview4).bind(cardList[position])
                holder.setIsRecyclable(false)
            }
        }
    }

    inner class MultiViewHolderPreview1(view: View) : RecyclerView.ViewHolder(view){
        private val question: TextView = view.findViewById(R.id.question)
        private val answer: TextView = view.findViewById(R.id.answer)

        fun bind(item: PreviewCardModel) {
            question.text = item.question
            answer.text = item.answer
        }
    }

    inner class MultiViewHolderPreview3(view: View) : RecyclerView.ViewHolder(view){
        private val question: TextView = view.findViewById(R.id.question)
        private val txtOption1: TextView = view.findViewById(R.id.choose_three_first_tv)
        private val txtOption2: TextView = view.findViewById(R.id.choose_three_second_tv)

        fun bind(item: PreviewCardModel) {
            question.text = item.question
            txtOption1.text = item.options!![0]
            txtOption2.text = item.options!![1]

            if (item.answer == item.options[0]) {
                txtOption1.setBackgroundResource(R.drawable.selected_option_container)
            } else {
                txtOption1.setBackgroundResource(R.drawable.search_container)
            }

            if (item.answer == item.options[1]) {
                txtOption2.setBackgroundResource(R.drawable.selected_option_container)
            } else {
                txtOption2.setBackgroundResource(R.drawable.search_container)
            }
        }
    }

    inner class MultiViewHolderPreview4(view: View) : RecyclerView.ViewHolder(view){
        private val question: TextView = view.findViewById(R.id.question)
        private val txtOption1: TextView = view.findViewById(R.id.choose_three_first_tv)
        private val txtOption2: TextView = view.findViewById(R.id.choose_three_second_tv)
        private val txtOption3: TextView = view.findViewById(R.id.choose_three_third_tv)


        fun bind(item: PreviewCardModel) {
            question.text = item.question
            txtOption1.text = item.options!![0]
            txtOption2.text = item.options!![1]
            txtOption3.text = item.options!![2]

            if (item.answer == item.options[0]) {
                txtOption1.setBackgroundResource(R.drawable.selected_option_container)
            } else {
                txtOption1.setBackgroundResource(R.drawable.search_container)
            }

            if (item.answer == item.options[1]) {
                txtOption2.setBackgroundResource(R.drawable.selected_option_container)
            } else {
                txtOption2.setBackgroundResource(R.drawable.search_container)
            }
            if (item.answer == item.options[2]) {
                txtOption3.setBackgroundResource(R.drawable.selected_option_container)
            } else {
                txtOption3.setBackgroundResource(R.drawable.search_container)
            }
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }



}