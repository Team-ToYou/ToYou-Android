package com.toyou.core.network.model.create

import com.google.gson.annotations.SerializedName

data class QuestionsDto(
    @SerializedName("questionList")
    val questions: List<CreateQuestion>
)

data class CreateQuestion(
    @SerializedName("questionId") val id: Long,
    @SerializedName("content") val content: String,
    @SerializedName("questionType") val type: String,
    @SerializedName("questioner") val questioner: String,
    @SerializedName("answerOption") val options: List<String>
)

data class HomeDto(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("emotion") val emotion: String?,
    @SerializedName("questionNum") val question: Int,
    @SerializedName("cardId") val id: Int?,
    @SerializedName("uncheckedAlarm") val alarm: Boolean
)

data class AnswerPost(
    @SerializedName("cardId") val id: Int
)

data class Answer(
    @SerializedName("questionId") val id: Long,
    @SerializedName("answer") val answer: String
)

data class AnswerRequest(
    @SerializedName("exposure") val exposure: Boolean,
    @SerializedName("answerList") val answers: List<Answer>
)

data class AnswerPatchRequest(
    @SerializedName("exposure") val exposure: Boolean,
    @SerializedName("answerList") val answers: List<Answer>,
    @SerializedName("cardId") val cardId: Int
)
