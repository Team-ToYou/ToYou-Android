package com.toyou.toyouandroid.presentation.fragment.notice

interface NoticeAdapterListener {
    fun onDeleteNotice(alarmId: Int, position: Int)
    fun onFriendRequestApprove(name: String, alarmId: Int, position: Int)
    fun onFriendRequestAcceptClick(item: NoticeItem.NoticeFriendRequestItem)
    fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem)
    fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem)
}