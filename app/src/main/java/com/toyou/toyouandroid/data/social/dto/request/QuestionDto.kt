package com.toyou.toyouandroid.data.social.dto.request

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("target") val target : String,
    @SerializedName("content") var content : String,
    @SerializedName("questionType") val type : String,
    @SerializedName("anonymous") var anonymous : Boolean = false,
    @SerializedName("answerOptionList") var options : List<String>?,
)