package com.toyou.toyouandroid.presentation.fragment.notice

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

data class NoticeDialogUiState(
    val title: String = "",
    val leftButtonText: String = "",
    val leftButtonClickAction: (() -> Unit)? = null
) : UiState

sealed interface NoticeDialogEvent : UiEvent {
    data object LeftButtonClicked : NoticeDialogEvent
}

sealed interface NoticeDialogAction : UiAction {
    data class SetDialogData(
        val title: String,
        val leftButtonText: String,
        val leftButtonClickAction: () -> Unit
    ) : NoticeDialogAction
    data object LeftButtonClick : NoticeDialogAction
}
