package com.toyou.toyouandroid.model

sealed class NoticeItem {
    data class NoticeFriendRequestItem(val nickname: String) : NoticeItem()
    data class NoticeFriendRequestAcceptedItem(val nickname: String) : NoticeItem()
    data class NoticeCardCheckItem(val nickname: String) : NoticeItem()
}
