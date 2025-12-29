package com.toyou.core.domain.model

data class Alarm(
    val alarmId: Int,
    val content: String,
    val nickname: String,
    val alarmType: AlarmType
)

enum class AlarmType {
    FRIEND_REQUEST,
    REQUEST_ACCEPTED,
    NEW_QUESTION,
    CARD_RECEIVED,
    OTHER;

    companion object {
        fun fromString(value: String): AlarmType = when (value.uppercase()) {
            "FRIEND_REQUEST" -> FRIEND_REQUEST
            "REQUEST_ACCEPTED" -> REQUEST_ACCEPTED
            "NEW_QUESTION" -> NEW_QUESTION
            "CARD_RECEIVED" -> CARD_RECEIVED
            else -> OTHER
        }
    }
}

data class AlarmList(
    val alarms: List<Alarm>
)

data class FriendRequest(
    val userId: Int,
    val nickname: String
)

data class FriendRequestList(
    val requests: List<FriendRequest>
)
