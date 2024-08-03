package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.multi_type1
import com.toyou.toyouandroid.model.multi_type2

class CardChooseAdapter(private val onItemClick: (Int, Boolean) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cardList: List<ChooseModel> = emptyList()

    fun setCards(cards: List<ChooseModel>) {
        this.cardList = cards
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
                    R.layout.item_rv_choose_card,
                    parent,
                    false
                )
                MultiViewHolder1(view, onItemClick)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_rv_choose_card,
                    parent,
                    false
                )
                MultiViewHolder2(view, onItemClick)
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

    inner class MultiViewHolder1(view: View, onItemClick: (Int, Boolean) -> Unit) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        fun bind(item: ChooseModel) {
            question.text = item.message
            txtOption1.text = item.options[0]
            txtOption2.text = item.options[1]
        }
    }

    inner class MultiViewHolder2(view: View, onItemClick: (Int, Boolean) -> Unit) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        private val txtOption3: TextView = view.findViewById(R.id.choose_option3)
        fun bind(item: ChooseModel) {
            question.text = item.message
            txtOption1.text = item.options[0]
            txtOption2.text = item.options[1]
            txtOption3.text = item.options[2]
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}
