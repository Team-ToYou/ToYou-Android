package com.toyou.toyouandroid.presentation.fragment.notice

interface NoticeAdapterListener {
    fun onDeleteNotice(alarmId: Int, position: Int)
    fun onShowDialog()
    fun onFriendRequestApprove(name: String)
    fun onFriendRequestItemClick(item: NoticeItem.NoticeFriendRequestItem)
    fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem)
    fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem)
}