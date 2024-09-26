package com.toyou.toyouandroid.data.create.dto.response

import com.google.gson.annotations.SerializedName

data class HomeDto(
    @SerializedName("nickname") val nickname : String,
    @SerializedName("emotion") val emotion : String?,
    @SerializedName("questionNum") val question : Int,
    @SerializedName("cardId") val id : Int?,
    @SerializedName("uncheckedAlarm") val alarm : Boolean
)