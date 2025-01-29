package com.toyou.toyouandroid.presentation.fragment.notice

interface NoticeAdapterListener {
    fun onDeleteNotice(alarmId: Int, position: Int)
    fun onFriendRequestApprove(id: Long, alarmId: Int, position: Int)
    fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem)
    fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem)
}