package com.toyou.toyouandroid.presentation.fragment.notice

interface NoticeAdapterListener {
    fun onDeleteNotice(alarmId: Int, position: Int)
    fun onShowDialog()
}