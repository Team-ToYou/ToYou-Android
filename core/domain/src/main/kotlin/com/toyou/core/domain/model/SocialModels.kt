package com.toyou.core.domain.model

data class FriendInfo(
    val id: Long,
    val nickname: String,
    val emotion: String?,
    val message: String?
)

data class FriendsList(
    val friends: List<FriendInfo>
)

data class SearchFriendResult(
    val userId: Long,
    val name: String,
    val status: FriendStatus
)

enum class FriendStatus {
    FRIEND,
    NOT_FRIEND,
    PENDING;

    companion object {
        fun fromString(value: String): FriendStatus = when (value.uppercase()) {
            "FRIEND" -> FRIEND
            "NOT_FRIEND" -> NOT_FRIEND
            "PENDING" -> PENDING
            else -> NOT_FRIEND
        }
    }
}

data class QuestionRequest(
    val targetId: Long,
    val content: String,
    val type: QuestionType,
    val anonymous: Boolean = false,
    val options: List<String>?
)

enum class QuestionType(val value: String) {
    SHORT("short"),
    LONG("long"),
    SELECT_ONE("selectOne"),
    SELECT_TWO("selectTwo");

    companion object {
        fun fromString(value: String): QuestionType = entries.find { it.value == value } ?: SHORT
    }
}
