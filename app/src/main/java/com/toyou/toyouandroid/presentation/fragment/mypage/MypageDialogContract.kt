package com.toyou.toyouandroid.presentation.fragment.mypage

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

data class MypageDialogUiState(
    val title: String = "",
    val subTitle: String? = null,
    val leftButtonText: String = "",
    val rightButtonText: String = "",
    val leftButtonTextColor: Int = 0,
    val rightButtonTextColor: Int = 0,
    val leftButtonClickAction: (() -> Unit)? = null,
    val rightButtonClickAction: (() -> Unit)? = null
) : UiState

sealed interface MypageDialogEvent : UiEvent {
    data object LeftButtonClicked : MypageDialogEvent
    data object RightButtonClicked : MypageDialogEvent
}

sealed interface MypageDialogAction : UiAction {
    data class SetDialogData(
        val title: String,
        val subTitle: String?,
        val leftButtonText: String,
        val rightButtonText: String,
        val leftButtonTextColor: Int,
        val rightButtonTextColor: Int,
        val leftButtonClickAction: () -> Unit,
        val rightButtonClickAction: () -> Unit
    ) : MypageDialogAction
    data object LeftButtonClick : MypageDialogAction
    data object RightButtonClick : MypageDialogAction
}
