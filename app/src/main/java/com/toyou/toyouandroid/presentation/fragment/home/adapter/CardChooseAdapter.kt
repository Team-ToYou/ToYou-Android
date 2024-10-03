package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.multi_type1
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel

class CardChooseAdapter(private val onItemClick: (Int, Boolean) -> Unit, private val cardViewModel: CardViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                    R.layout.item_rv_choose_two_card,
                    parent,
                    false
                )
                MultiViewHolder1(view, onItemClick, cardViewModel)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_rv_choose_card,
                    parent,
                    false
                )
                MultiViewHolder2(view, onItemClick, cardViewModel)
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

    inner class MultiViewHolder1(view: View, onItemClick: (Int, Boolean) -> Unit, private val cardViewModel: CardViewModel) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        private val fromWho : TextView = view.findViewById(R.id.fromWho_tv)
        private val button: Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val btnFrame : FrameLayout = itemView.findViewById(R.id.btn_frame)

        init {
            // 기본적으로 클릭 리스너는 항상 동작하도록 설정
            btnFrame.setOnClickListener {
                val currentCount = cardViewModel.countSelection.value ?: 0
                if (isSelected) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                } else if (currentCount < 5) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                } else{
                    Toast.makeText(itemView.context, "질문은 최대 5개까지 선택할 수 있습니다", Toast.LENGTH_SHORT).show()
                }
            }

        }

        fun bind(item: ChooseModel) {
            question.text = item.message
            txtOption1.text = item.options[0]
            txtOption2.text = item.options[1]
            fromWho.text = "From. ${item.fromWho}"
            isSelected = item.isButtonSelected
            updateButtonBackground(isSelected)
        }
        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes = if (isSelected) R.drawable.create_check else R.drawable.create_uncheck
            button.setBackgroundResource(backgroundRes)
        }
    }

    inner class MultiViewHolder2(view: View, onItemClick: (Int, Boolean) -> Unit, private val cardViewModel: CardViewModel) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.textMessage)
        private val txtOption1: TextView = view.findViewById(R.id.choose_option1)
        private val txtOption2: TextView = view.findViewById(R.id.choose_option2)
        private val txtOption3: TextView = view.findViewById(R.id.choose_option3)
        private val fromWho : TextView = view.findViewById(R.id.fromWho_tv)
        private val button: Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val btnFrame : FrameLayout = itemView.findViewById(R.id.btn_frame)

        init {
            // 기본적으로 클릭 리스너는 항상 동작하도록 설정
            btnFrame.setOnClickListener {
                val currentCount = cardViewModel.countSelection.value ?: 0
                if (isSelected) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                } else if (currentCount < 5) {
                    isSelected = !isSelected
                    updateButtonBackground(isSelected)
                    onItemClick(adapterPosition, isSelected)
                } else{
                    Toast.makeText(itemView.context, "질문은 최대 5개까지 선택할 수 있습니다", Toast.LENGTH_SHORT).show()
                }
            }

        }
        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes = if (isSelected) R.drawable.create_check else R.drawable.create_uncheck
            button.setBackgroundResource(backgroundRes)
        }

        fun bind(item: ChooseModel) {
            question.text = item.message
            txtOption1.text = item.options[0]
            txtOption2.text = item.options[1]
            txtOption3.text = item.options[2]
            fromWho.text = "From. ${item.fromWho}"
            isSelected = item.isButtonSelected
            updateButtonBackground(isSelected)

        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

}
