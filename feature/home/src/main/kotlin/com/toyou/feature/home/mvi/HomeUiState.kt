package com.toyou.feature.home.viewmodel

import com.toyou.core.common.AppConstants
import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.core.domain.model.YesterdayCardInfo

data class HomeUiState(
    val currentDate: String = "",
    val emotionText: String = AppConstants.Ui.DEFAULT_EMOTION_TEXT,
    val yesterdayCards: List<YesterdayCardInfo> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
) : UiState

sealed interface HomeEvent : UiEvent {
    data class ShowError(val message: String) : HomeEvent
    data object TokenExpired : HomeEvent
}

sealed interface HomeAction : UiAction {
    data object LoadYesterdayCards : HomeAction
    data class UpdateEmotion(val emotionText: String) : HomeAction
    data object ResetState : HomeAction
}
