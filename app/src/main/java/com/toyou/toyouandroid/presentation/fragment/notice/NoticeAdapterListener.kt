package com.toyou.toyouandroid.presentation.fragment.notice

import com.toyou.toyouandroid.model.NoticeItem

interface NoticeAdapterListener {
    fun onDeleteNotice(alarmId: Int, position: Int)
    fun onShowDialog()
    fun onFriendRequestItemClick(item: NoticeItem.NoticeFriendRequestItem)
    fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem)
    fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem)

}