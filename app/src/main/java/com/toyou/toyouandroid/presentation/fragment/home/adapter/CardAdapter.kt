package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.CardModel

class CardAdapter(private val onItemClick: (Int, Boolean) -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var cardList: List<CardModel> = emptyList()

    fun setCards(cards: List<CardModel>) {
        this.cardList = cards
        Log.d("CardAdapter", "setCards called with: $cards")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_card, parent, false)
        return CardViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int = cardList.size

    class CardViewHolder(itemView: View, onItemClick: (Int, Boolean) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val cardMessageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val button: Button = itemView.findViewById(R.id.button)
        private var isSelected: Boolean = false
        private val editText: EditText = itemView.findViewById(R.id.memo_et)
        private val fromWho: TextView = itemView.findViewById(R.id.fromWho_tv)
        private val fromWhoEt: TextView = itemView.findViewById(R.id.fromWhoEt_tv)
        private val wordCount: TextView = itemView.findViewById(R.id.limit_200)
        private val chooseLayouts = listOf(
            itemView.findViewById<LinearLayout>(R.id.choose_two_linear),
            itemView.findViewById<LinearLayout>(R.id.choose_three_linear),
            itemView.findViewById<LinearLayout>(R.id.choose_forth_linear),
            itemView.findViewById<LinearLayout>(R.id.choose_five_linear)
        )
        private val fromTexts = listOf(
            itemView.findViewById<TextView>(R.id.fromWhoTwo_tv),
            itemView.findViewById<TextView>(R.id.fromWhoThree_tv),
            itemView.findViewById<TextView>(R.id.fromWhoFour_tv),
            itemView.findViewById<TextView>(R.id.fromWhoFive_tv)
        )
        private var chooseTwoAnswer1Selected: Boolean = false
        private var chooseTwoAnswer2Selected: Boolean = false
        private val chooseTwoAnswer1: TextView = itemView.findViewById(R.id.choose_two_first_tv)
        private val chooseTwoAnswer2: TextView = itemView.findViewById(R.id.choose_two_second_tv)
        private val chooseButtonsFive = listOf(
            itemView.findViewById<TextView>(R.id.choose_five_first_tv),
            itemView.findViewById<TextView>(R.id.choose_five_second_tv),
            itemView.findViewById<TextView>(R.id.choose_five_third_tv),
            itemView.findViewById<TextView>(R.id.choose_five_forth_tv),
            itemView.findViewById<TextView>(R.id.choose_five_fifth_tv)
        )
        private var selectedButtonIndex: Int? = null
        private val chooseButtonsFour = listOf(
            itemView.findViewById<TextView>(R.id.choose_forth_first_tv),
            itemView.findViewById<TextView>(R.id.choose_forth_second_tv),
            itemView.findViewById<TextView>(R.id.choose_forth_third_tv),
            itemView.findViewById<TextView>(R.id.choose_forth_forth_tv),
        )
        private var selectedButtonIndexFour: Int? = null
        private val chooseButtonsFThree = listOf(
            itemView.findViewById<TextView>(R.id.choose_three_first_tv),
            itemView.findViewById<TextView>(R.id.choose_three_second_tv),
            itemView.findViewById<TextView>(R.id.choose_three_third_tv),
        )
        private var selectedButtonIndexThree: Int? = null
        private val chooseButtonsTwo = listOf(
            itemView.findViewById<TextView>(R.id.choose_two_first_tv),
            itemView.findViewById<TextView>(R.id.choose_two_second_tv),
        )
        private var selectedButtonTwo: Int? = null


        var message: String = ""

        init {
            button.setOnClickListener {
                isSelected = !isSelected
                updateButtonBackground(isSelected)
                onItemClick(adapterPosition, isSelected)
            }

            editText.addTextChangedListener(object : TextWatcher {
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

            chooseButtonsFive.forEachIndexed { index, textView ->
                textView.setOnClickListener {
                    toggleSelection(index, chooseButtonsFive)
                }
            }

            chooseButtonsFour.forEachIndexed { index, textView ->
                textView.setOnClickListener {
                    toggleSelection(index, chooseButtonsFour)
                }
            }
            chooseButtonsFThree.forEachIndexed { index, textView ->
                textView.setOnClickListener {
                    toggleSelection(index, chooseButtonsFThree)
                }
            }
            chooseButtonsTwo.forEachIndexed { index, textView ->
                textView.setOnClickListener {
                    toggleSelection(index, chooseButtonsTwo)
                }
            }
        }

        fun bind(card: CardModel) {
            cardMessageTextView.text = card.message
            fromWhoEt.text = if (isSelected) card.fromWho else ""
            fromWho.text = if (!isSelected) card.fromWho else ""

            fromTexts.forEach { it.text = card.fromWho }

            when (card.questionType) {
                1 -> updateVisibility(isSelected, editText, wordCount, fromWhoEt)
                2 -> {
                    updateVisibility(isSelected, chooseLayouts[0], fromTexts[0])
                }

                3 -> updateVisibility(isSelected, chooseLayouts[1], fromTexts[1])
                4 -> updateVisibility(isSelected, chooseLayouts[2], fromTexts[2])
                5 -> updateVisibility(isSelected, chooseLayouts[3], fromTexts[3])
            }
        }

        private fun toggleSelection(buttonIndex: Int, list: List<View>) {
            selectedButtonIndex = if (selectedButtonIndex == buttonIndex) null else buttonIndex
            updateButtonColors(list)
        }

        private fun updateButtonColors(list: List<View>) {
            list.forEachIndexed { index, textView ->
                val isSelected = index == selectedButtonIndex
                val backgroundRes =
                    if (isSelected) R.drawable.tv_selector else R.drawable.search_container
                textView.setBackgroundResource(backgroundRes)
                textView.isSelected = isSelected
            }
        }


        private fun updateButtonBackground(isSelected: Boolean) {
            val backgroundRes =
                if (isSelected) R.drawable.create_unclicked_btn else R.drawable.create_clicked_btn
            button.setBackgroundResource(backgroundRes)
        }

        private fun updateVisibility(isSelected: Boolean, vararg views: View) {
            views.forEach { it.visibility = if (isSelected) View.VISIBLE else View.GONE }
        }
    }

}
