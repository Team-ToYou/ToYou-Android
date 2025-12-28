package com.toyou.toyouandroid.presentation.fragment.home

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.data.emotion.dto.DiaryCard
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCard

data class HomeUiState(
    val currentDate: String = "",
    val emotionText: String = "멘트",
    val diaryCards: List<DiaryCard>? = null,
    val yesterdayCards: List<YesterdayCard> = emptyList(),
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