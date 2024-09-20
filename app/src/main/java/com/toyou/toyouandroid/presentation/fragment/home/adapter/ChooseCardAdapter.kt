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
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.PreviewChooseModel
import com.toyou.toyouandroid.model.multi_type1
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel

class ChooseCardAdapter(private val cardViewModel: CardViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cardList: List<PreviewCardModel> = emptyList()


    fun setCards(cards: List<PreviewCardModel>) {
        notifyDataSetChanged()
        this.cardList = cards.filter { it.type == 2 || it.type == 3 }
        Log.d("선택4", cards.toString())
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

    inner class MultiViewHolder1(view: View) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        private val fromWho: TextView = view.findViewById(R.id.fromWho_tv)
        private var selectedOption: TextView? = null

        init {
            txtOption1.setOnClickListener {
                handleOptionSelection(txtOption1)
                cardList[adapterPosition].answer = txtOption1.text.toString()
                cardViewModel.updateCardInputStatusChoose(adapterPosition, true)
            }
            txtOption2.setOnClickListener {
                handleOptionSelection(txtOption2)
                cardList[adapterPosition].answer = txtOption2.text.toString()
                cardViewModel.updateCardInputStatusChoose(adapterPosition, true)
            }
        }

        private fun handleOptionSelection(selected: TextView) {
            selectedOption?.isSelected = false
            selected.isSelected = true
            selectedOption = selected
        }

        fun bind(item: PreviewCardModel) {
            question.text = item.question
            txtOption1.text = item.options!![0]
            txtOption2.text = item.options!![1]
            fromWho.text = item.fromWho

            when (item.answer) {
                txtOption1.text.toString() -> handleOptionSelection(txtOption1)
                txtOption2.text.toString() -> handleOptionSelection(txtOption2)
            }
        }
    }

    inner class MultiViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        private val txtOption3: TextView = view.findViewById(R.id.choose_option3)
        private val fromWho: TextView = view.findViewById(R.id.fromWho_tv)
        private var selectedOption: TextView? = null

        init {
            txtOption1.setOnClickListener {
                handleOptionSelection(txtOption1)
                cardList[adapterPosition].answer = txtOption1.text.toString()
                cardViewModel.updateCardInputStatusChoose(adapterPosition, true)
            }
            txtOption2.setOnClickListener {
                handleOptionSelection(txtOption2)
                cardList[adapterPosition].answer = txtOption2.text.toString()
                cardViewModel.updateCardInputStatusChoose(adapterPosition, true)
            }
            txtOption3.setOnClickListener {
                handleOptionSelection(txtOption3)
                cardList[adapterPosition].answer = txtOption3.text.toString()
                cardViewModel.updateCardInputStatusChoose(adapterPosition, true)
            }
        }

        private fun handleOptionSelection(selected: TextView) {
            selectedOption?.isSelected = false
            selected.isSelected = true
            selectedOption = selected
        }

        fun bind(item: PreviewCardModel) {
            question.text = item.question
            txtOption1.text = item.options!![0]
            txtOption2.text = item.options!![1]
            txtOption3.text = item.options!![2]
            fromWho.text = item.fromWho

            when (item.answer) {
                txtOption1.text.toString() -> handleOptionSelection(txtOption1)
                txtOption2.text.toString() -> handleOptionSelection(txtOption2)
                txtOption3.text.toString() -> handleOptionSelection(txtOption3)
            }
        }
    }


    override fun getItemCount(): Int {
        return cardList.size
    }

}
