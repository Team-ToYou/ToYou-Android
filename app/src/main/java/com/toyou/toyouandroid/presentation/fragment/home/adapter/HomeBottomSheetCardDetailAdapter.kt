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
    //private lateinit var emotion: String
    private var emotion: String = "NORMAL"

    fun setCards(newCards: List<YesterdayCard>) {
        questionList = newCards.flatMap { it.cardContent.questionList }
        notifyDataSetChanged()
    }

    fun setEmotion(emotion : String){
        this.emotion = emotion
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
                    R.layout.card_qa_list, parent, false
                )
                MultiViewHolderPreview1(view)
            }
            3 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_answer_option_two, parent, false
                )
                MultiViewHolderPreview3(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_answer_option_three, parent, false
                )
                MultiViewHolderPreview4(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = questionList[position]

        when (item.type) {
            "LONG_ANSWER", "SHORT_ANSWER" -> {
                (holder as MultiViewHolderPreview1).bind(
                    PreviewCardModel(item.content, item.answer,mapQuestionType(item.type),item.questioner,item.options, item.id)
                )
                holder.setIsRecyclable(false)
            }
            "OPTIONAL" -> {
                (holder as MultiViewHolderPreview3).bind(
                    PreviewCardModel(item.content, item.answer,mapQuestionType(item.type),item.questioner,item.options, item.id)
                )
                holder.setIsRecyclable(false)
            }
            else -> {
                (holder as MultiViewHolderPreview4).bind(
                    PreviewCardModel(item.content, item.answer,mapQuestionType(item.type),item.questioner,item.options, item.id)
                )
                holder.setIsRecyclable(false)
            }
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
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
            txtOption2.text = item.options[1]

            if (item.answer == item.options[0]) {
                when(emotion){
                    "HAPPY" -> txtOption1.setBackgroundResource(R.drawable.selected_option_happy)
                    "EXCITED" -> txtOption1.setBackgroundResource(R.drawable.selected_option_excited)
                    "NORMAL" -> txtOption1.setBackgroundResource(R.drawable.selected_option_normal)
                    "NERVOUS" -> txtOption1.setBackgroundResource(R.drawable.selected_option_container)
                    "ANGRY" -> txtOption1.setBackgroundResource(R.drawable.selected_option_angry)
                }
            } else {
                txtOption1.setBackgroundResource(R.drawable.search_container)
            }

            if (item.answer == item.options[1]) {
                when(emotion){
                    "HAPPY" -> txtOption2.setBackgroundResource(R.drawable.selected_option_happy)
                    "EXCITED" -> txtOption2.setBackgroundResource(R.drawable.selected_option_excited)
                    "NORMAL" -> txtOption2.setBackgroundResource(R.drawable.selected_option_normal)
                    "NERVOUS" -> txtOption2.setBackgroundResource(R.drawable.selected_option_container)
                    "ANGRY" -> txtOption2.setBackgroundResource(R.drawable.selected_option_angry)
                }
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
            txtOption2.text = item.options[1]
            txtOption3.text = item.options[2]

            if (item.answer == item.options[0]) {
                when(emotion){
                    "HAPPY" -> txtOption1.setBackgroundResource(R.drawable.selected_option_happy)
                    "EXCITED" -> txtOption1.setBackgroundResource(R.drawable.selected_option_excited)
                    "NORMAL" -> txtOption1.setBackgroundResource(R.drawable.selected_option_normal)
                    "NERVOUS" -> txtOption1.setBackgroundResource(R.drawable.selected_option_container)
                    "ANGRY" -> txtOption1.setBackgroundResource(R.drawable.selected_option_angry)
                }
            } else {
                txtOption1.setBackgroundResource(R.drawable.search_container)
            }

            if (item.answer == item.options[1]) {
                when(emotion){
                    "HAPPY" -> txtOption2.setBackgroundResource(R.drawable.selected_option_happy)
                    "EXCITED" -> txtOption2.setBackgroundResource(R.drawable.selected_option_excited)
                    "NORMAL" -> txtOption2.setBackgroundResource(R.drawable.selected_option_normal)
                    "NERVOUS" -> txtOption2.setBackgroundResource(R.drawable.selected_option_container)
                    "ANGRY" -> txtOption2.setBackgroundResource(R.drawable.selected_option_angry)
                }
            } else {
                txtOption2.setBackgroundResource(R.drawable.search_container)
            }
            if (item.answer == item.options[2]) {
                when(emotion){
                    "HAPPY" -> txtOption3.setBackgroundResource(R.drawable.selected_option_happy)
                    "EXCITED" -> txtOption3.setBackgroundResource(R.drawable.selected_option_excited)
                    "NORMAL" -> txtOption3.setBackgroundResource(R.drawable.selected_option_normal)
                    "NERVOUS" -> txtOption3.setBackgroundResource(R.drawable.selected_option_container)
                    "ANGRY" -> txtOption3.setBackgroundResource(R.drawable.selected_option_angry)
                }
            } else {
                txtOption3.setBackgroundResource(R.drawable.search_container)
            }
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