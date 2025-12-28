package com.toyou.toyouandroid.presentation.fragment.notice

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

data class NoticeUiState(
    val noticeItems: List<NoticeItem> = emptyList(),
    val generalNotices: List<NoticeItem> = emptyList(),
    val friendRequestNotices: List<NoticeItem.NoticeFriendRequestItem> = emptyList(),
    val hasNotifications: Boolean = false,
    val isLoading: Boolean = false
) : UiState

sealed interface NoticeEvent : UiEvent {
    data class ShowError(val message: String) : NoticeEvent
    data object TokenExpired : NoticeEvent
    data class NoticeDeleted(val alarmId: Int, val position: Int) : NoticeEvent
    data class NoticeDeleteFailed(val alarmId: Int, val position: Int) : NoticeEvent
}

sealed interface NoticeAction : UiAction {
    data object FetchNotices : NoticeAction
    data object FetchFriendsRequestNotices : NoticeAction
    data class DeleteNotice(val alarmId: Int, val position: Int) : NoticeAction
}
