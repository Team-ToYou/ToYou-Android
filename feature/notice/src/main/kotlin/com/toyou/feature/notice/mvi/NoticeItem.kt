package com.toyou.feature.notice.viewmodel

sealed class NoticeItem(open val alarmId: Int) {
    data class NoticeFriendRequestItem(val nickname: String, override val alarmId: Int) : NoticeItem(alarmId)
    data class NoticeFriendRequestAcceptedItem(val nickname: String, override val alarmId: Int) : NoticeItem(alarmId)
    data class NoticeCardCheckItem(val nickname: String, override val alarmId: Int) : NoticeItem(alarmId)
}
