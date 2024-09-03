package com.toyou.toyouandroid.fcm.dto.request

import com.google.gson.annotations.SerializedName

data class Token (
    @SerializedName("token") val token :String
)