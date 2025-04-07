package com.toyou.toyouandroid.presentation.fragment.record.friend

import java.util.Date

interface OnFriendDateClickListener {
    fun onDateClick(date: Date)
    fun onFriendClick(cardId: Int?)
}