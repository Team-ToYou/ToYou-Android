package com.toyou.toyouandroid.presentation.fragment.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCard
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCardQuestion
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.Ytype1
import com.toyou.toyouandroid.model.type1
import com.toyou.toyouandroid.model.type2
import com.toyou.toyouandroid.model.type3

class HomeBottomSheetCardDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var questionList: List<YesterdayCardQuestion> = emptyList()
    private var emotionMap: Map<Long, String> = emptyMap() // cardId -> emotion
    private var questionToCardMap: Map<Long, Long> = emptyMap() // questionId -> cardId

    fun setCards(newCards: List<YesterdayCard>) {
        questionList = newCards.flatMap { it.cardContent.questionList }

        // 카드 ID별 감정을 저장
        emotionMap = newCards.associate { it.cardId to it.cardContent.emotion }

        // 질문 ID별 카드 ID를 저장
        questionToCardMap = newCards.flatMap { card ->
            card.cardContent.questionList.map { it.id to card.cardId }
        }.toMap()

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (questionList[position].type) {
            "LONG_ANSWER" -> 1
            "SHORT_ANSWER" -> 2
            "OPTIONAL" -> 3
            else -> 4
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            1, 2 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_yesterday_qa, parent, false
                )
                MultiViewHolderPreview1(view)
            }
            3 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_yesterday_option_two, parent, false
                )
                MultiViewHolderPreview3(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_yesterday_option_three, parent, false
                )
                MultiViewHolderPreview4(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = questionList[position]

        // 해당 질문이 속한 카드의 ID 찾기
        val cardId = questionToCardMap[item.id] ?: -1
        // 카드 ID를 통해 감정을 찾기 (없으면 기본값 "ANGRY")
        val emotion = emotionMap[cardId] ?: "ANGRY"

        when (item.type) {
            "LONG_ANSWER", "SHORT_ANSWER" -> {
                (holder as MultiViewHolderPreview1).bind(
                    PreviewCardModel(item.content, item.answer, mapQuestionType(item.type), item.questioner, item.options, item.id)
                )
            }
            "OPTIONAL" -> {
                (holder as MultiViewHolderPreview3).bind(
                    PreviewCardModel(item.content, item.answer, mapQuestionType(item.type), item.questioner, item.options, item.id),
                    emotion
                )
            }
            else -> {
                (holder as MultiViewHolderPreview4).bind(
                    PreviewCardModel(item.content, item.answer, mapQuestionType(item.type), item.questioner, item.options, item.id),
                    emotion
                )
            }
        }
    }

    override fun getItemCount(): Int = questionList.size

    inner class MultiViewHolderPreview1(view: View) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.question)
        private val answer: TextView = view.findViewById(R.id.answer)

        fun bind(item: PreviewCardModel) {
            question.text = item.question
            answer.text = item.answer
        }
    }

    inner class MultiViewHolderPreview3(view: View) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.question)
        private val txtOption1: TextView = view.findViewById(R.id.choose_three_first_tv)
        private val txtOption2: TextView = view.findViewById(R.id.choose_three_second_tv)

        fun bind(item: PreviewCardModel, emotion: String) {
            question.text = item.question
            txtOption1.text = item.options!![0]
            txtOption2.text = item.options[1]

            applyEmotionStyle(txtOption1, item.answer == item.options[0], emotion)
            applyEmotionStyle(txtOption2, item.answer == item.options[1], emotion)
        }
    }

    inner class MultiViewHolderPreview4(view: View) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.question)
        private val txtOption1: TextView = view.findViewById(R.id.choose_three_first_tv)
        private val txtOption2: TextView = view.findViewById(R.id.choose_three_second_tv)
        private val txtOption3: TextView = view.findViewById(R.id.choose_three_third_tv)

        fun bind(item: PreviewCardModel, emotion: String) {
            question.text = item.question
            txtOption1.text = item.options!![0]
            txtOption2.text = item.options[1]
            txtOption3.text = item.options[2]

            applyEmotionStyle(txtOption1, item.answer == item.options[0], emotion)
            applyEmotionStyle(txtOption2, item.answer == item.options[1], emotion)
            applyEmotionStyle(txtOption3, item.answer == item.options[2], emotion)
        }
    }

    private fun applyEmotionStyle(view: TextView, isSelected: Boolean, emotion: String) {
        if (isSelected) {
            when (emotion) {
                "HAPPY" -> view.setBackgroundResource(R.drawable.selected_option_happy)
                "EXCITED" -> view.setBackgroundResource(R.drawable.selected_option_excited)
                "NORMAL" -> view.setBackgroundResource(R.drawable.selected_option_normal)
                "NERVOUS" -> view.setBackgroundResource(R.drawable.selected_option_container)
                "ANGRY" -> view.setBackgroundResource(R.drawable.selected_option_angry)
                else -> view.setBackgroundResource(R.drawable.search_container)
            }
        } else {
            view.setBackgroundResource(R.drawable.search_container)
        }
    }

    fun mapQuestionType(type: String): Int {
        return when (type) {
            "LONG_ANSWER" -> 1
            "SHORT_ANSWER" -> 2
            "OPTIONAL" -> 3
            else -> 4
        }
    }
}
