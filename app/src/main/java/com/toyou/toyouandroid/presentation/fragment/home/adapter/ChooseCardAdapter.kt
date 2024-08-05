package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.PreviewChooseModel
import com.toyou.toyouandroid.model.multi_type1

class ChooseCardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cardList: List<PreviewChooseModel> = emptyList()

    fun setCards(cards: List<PreviewChooseModel>) {
        this.cardList = cards
        Log.d("선택4", cards.toString())
        notifyDataSetChanged()
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
            multi_type1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_rv_edit_two,
                    parent,
                    false
                )
                MultiViewHolder1(view)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_rv_edit_three,
                    parent,
                    false
                )
                MultiViewHolder2(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (cardList[position].type) {
            multi_type1 -> {
                (holder as MultiViewHolder1).bind(cardList[position])
                holder.setIsRecyclable(false)
            }

            else -> {
                (holder as MultiViewHolder2).bind(cardList[position])
                holder.setIsRecyclable(false)
            }
        }
    }

    inner class MultiViewHolder1(view: View) : RecyclerView.ViewHolder(view){
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        private val fromWho : TextView = view.findViewById(R.id.fromWho_tv)
        private var selectedOption: TextView? = null


        init {
            txtOption1.setOnClickListener {
                handleOptionSelection(txtOption1)
            }
            txtOption2.setOnClickListener {
                handleOptionSelection(txtOption2)
            }
        }

        private fun handleOptionSelection(selected: TextView) {
            selectedOption?.isSelected = false
            selected.isSelected = true
            selectedOption = selected
        }

        fun bind(item: PreviewChooseModel) {
            question.text = item.message
            txtOption1.text = item.options[0]
            txtOption2.text = item.options[1]
            fromWho.text = item.fromWho
        }
    }

    inner class MultiViewHolder2(view: View) : RecyclerView.ViewHolder(view){
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        private val txtOption3: TextView = view.findViewById(R.id.choose_option3)
        private val fromWho : TextView = view.findViewById(R.id.fromWho_tv)
        private var selectedOption: TextView? = null

        init {
            txtOption1.setOnClickListener {
                handleOptionSelection(txtOption1)
            }
            txtOption2.setOnClickListener {
                handleOptionSelection(txtOption2)
            }
            txtOption3.setOnClickListener {
                handleOptionSelection(txtOption3)
            }
        }

        private fun handleOptionSelection(selected: TextView) {
            selectedOption?.isSelected = false
            selected.isSelected = true
            selectedOption = selected
        }
        fun bind(item: PreviewChooseModel) {
            question.text = item.message
            txtOption1.text = item.options[0]
            txtOption2.text = item.options[1]
            txtOption3.text = item.options[2]
            fromWho.text = item.fromWho

        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

}