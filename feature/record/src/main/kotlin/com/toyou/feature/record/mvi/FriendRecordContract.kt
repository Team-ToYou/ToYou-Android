package com.toyou.feature.record.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.core.domain.model.DiaryCardNum
import com.toyou.core.domain.model.DiaryCardPerDay

data class FriendRecordUiState(
    val diaryCardsNum: List<DiaryCardNum> = emptyList(),
    val diaryCardPerDay: List<DiaryCardPerDay> = emptyList(),
    val isLoading: Boolean = false
) : UiState

sealed interface FriendRecordEvent : UiEvent {
    data class ShowError(val message: String) : FriendRecordEvent
    data object TokenExpired : FriendRecordEvent
}

sealed interface FriendRecordAction : UiAction {
    data class LoadDiaryCardsNum(val year: Int, val month: Int) : FriendRecordAction
    data class LoadDiaryCardPerDay(val year: Int, val month: Int, val day: Int) : FriendRecordAction
}
