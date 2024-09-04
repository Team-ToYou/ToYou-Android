package com.toyou.toyouandroid.fcm.dto.request

import com.google.gson.annotations.SerializedName

data class FCM(
    @SerializedName("token") val token : String,
    @SerializedName("title") val title : String,
    @SerializedName("body") val body : String,
)