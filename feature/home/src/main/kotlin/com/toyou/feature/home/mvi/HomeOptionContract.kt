package com.toyou.feature.home.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.core.network.model.emotion.EmotionResponse

data class HomeOptionUiState(
    val emotionResponse: EmotionResponse? = null,
    val isLoading: Boolean = false
) : UiState

sealed interface HomeOptionEvent : UiEvent {
    data class ShowError(val message: String) : HomeOptionEvent
    data object TokenExpired : HomeOptionEvent
}

sealed interface HomeOptionAction : UiAction {
    data class UpdateEmotion(val emotionRequest: com.toyou.core.network.model.emotion.EmotionRequest) : HomeOptionAction
}
