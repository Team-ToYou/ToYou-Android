package com.toyou.toyouandroid.data.create.dto.request

import com.google.gson.annotations.SerializedName

data class AnswerDto(
    @SerializedName("questionList") val answers : List<Answer>,
    @SerializedName("exposure") val exposure : Boolean
)

data class Answer(
    @SerializedName("questionId") val id : Long,
    @SerializedName("answer") val answer : String
)
