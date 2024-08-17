package com.toyou.toyouandroid.data.social.dto.request

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("target") val target : String,
    @SerializedName("content") val content : String,
    @SerializedName("questionType") val type : String,
    @SerializedName("anonymous") val anonymous : String,
    @SerializedName("answerOptionList") val options : List<String>?,
)

