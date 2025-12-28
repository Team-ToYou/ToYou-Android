package com.toyou.toyouandroid.presentation.fragment.record

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.PreviewCardModel

data class CardInfoUiState(
    val cards: List<CardModel> = emptyList(),
    val previewCards: List<PreviewCardModel> = emptyList(),
    val exposure: Boolean = false,
    val answer: String = "",
    val cardId: Int = 0,
    val date: String = "",
    val emotion: String = "",
    val receiver: String = "",
    val isLoading: Boolean = false
) : UiState

sealed interface CardInfoEvent : UiEvent {
    data class ShowError(val message: String) : CardInfoEvent
    data object CardDetailLoaded : CardInfoEvent
    data object CardDetailFailed : CardInfoEvent
}

sealed interface CardInfoAction : UiAction {
    data class GetCardDetail(val id: Long) : CardInfoAction
    data class UpdateAnswer(val answer: String) : CardInfoAction
}
