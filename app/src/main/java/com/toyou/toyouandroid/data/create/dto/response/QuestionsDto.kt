package com.toyou.toyouandroid.data.create.dto.response

import com.google.gson.annotations.SerializedName

data class QuestionsDto(
    @SerializedName("questionList")
    val questions: List<Question>
)
data class Question(
    @SerializedName("questionId") val id: Long,
    @SerializedName("content") val content: String,
    @SerializedName("questionType") val type: String,
    @SerializedName("questioner") val questioner: String,
    @SerializedName("answerOption") val options: List<String>
)