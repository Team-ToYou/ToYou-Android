package com.toyou.toyouandroid.data.notice.dto

import com.google.gson.annotations.SerializedName

data class AlarmResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: AlarmResult
)

data class AlarmResult(
    @SerializedName("alarmList")
    val alarmList: List<Alarm>
)

data class Alarm(
    @SerializedName("alarmId")
    val alarmId: Int,

    @SerializedName("content")
    val content: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("alarmType")
    val alarmType: String
)

data class AlarmDeleteResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String
)

data class FriendsRequestResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: FriendsRequestResult
)

data class FriendsRequestResult(
    @SerializedName("senderInfos")
    val friendsRequestList: List<FriendsRequest>
)

data class FriendsRequest(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("nickname")
    val nickname: String
)