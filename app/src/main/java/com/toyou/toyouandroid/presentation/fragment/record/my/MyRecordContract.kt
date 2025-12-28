package com.toyou.toyouandroid.presentation.fragment.record.my

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.data.record.dto.DiaryCard

data class MyRecordUiState(
    val diaryCards: List<DiaryCard> = emptyList(),
    val isLoading: Boolean = false
) : UiState

sealed interface MyRecordEvent : UiEvent {
    data class ShowError(val message: String) : MyRecordEvent
    data object DeleteSuccess : MyRecordEvent
    data object PatchSuccess : MyRecordEvent
}

sealed interface MyRecordAction : UiAction {
    data class LoadDiaryCards(val year: Int, val month: Int) : MyRecordAction
    data class DeleteDiaryCard(val cardId: Int) : MyRecordAction
    data class PatchDiaryCard(val cardId: Int) : MyRecordAction
}
