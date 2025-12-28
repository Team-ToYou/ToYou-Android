package com.toyou.toyouandroid.presentation.fragment.record

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

data class CalendarDialogUiState(
    val title: String = "",
    val leftButtonText: String = "",
    val rightButtonText: String = "",
    val leftButtonTextColor: Int = 0,
    val rightButtonTextColor: Int = 0,
    val leftButtonClickAction: (() -> Unit)? = null,
    val rightButtonClickAction: (() -> Unit)? = null
) : UiState

sealed interface CalendarDialogEvent : UiEvent {
    data object LeftButtonClicked : CalendarDialogEvent
    data object RightButtonClicked : CalendarDialogEvent
}

sealed interface CalendarDialogAction : UiAction {
    data class SetDialogData(
        val title: String,
        val leftButtonText: String,
        val rightButtonText: String,
        val leftButtonTextColor: Int,
        val rightButtonTextColor: Int,
        val leftButtonClickAction: () -> Unit,
        val rightButtonClickAction: () -> Unit
    ) : CalendarDialogAction
    data object LeftButtonClick : CalendarDialogAction
    data object RightButtonClick : CalendarDialogAction
}
