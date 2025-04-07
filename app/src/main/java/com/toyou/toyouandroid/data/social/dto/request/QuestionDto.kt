package com.toyou.toyouandroid.data.social.dto.request

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("targetId") val targetId : Long,
    @SerializedName("content") var content : String,
    @SerializedName("questionType") val type : String,
    @SerializedName("anonymous") var anonymous : Boolean = false,
    @SerializedName("answerOptionList") var options : List<String>?,
)