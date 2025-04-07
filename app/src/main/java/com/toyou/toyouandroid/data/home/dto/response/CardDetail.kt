package com.toyou.toyouandroid.data.home.dto.response

import com.google.gson.annotations.SerializedName
import com.toyou.toyouandroid.data.create.dto.request.Answer

data class CardDetail(
    @SerializedName("date") val date : String,
    @SerializedName("receiver") val receiver: String,
    @SerializedName("emotion") val emotion : String,
    @SerializedName("exposure") val exposure : Boolean,
    @SerializedName("questionList") val questions : List<AnswerQuestion>,
)

data class AnswerQuestion(
    @SerializedName("questionId") val id : Long,
    @SerializedName("content") val content : String,
    @SerializedName("questionType") val type: String,
    @SerializedName("questioner") val questioner : String,
    @SerializedName("answer") val answer : String,
    @SerializedName("answerOption") val options : List<String>?,
)

