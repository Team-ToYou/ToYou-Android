package com.toyou.feature.record.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.core.domain.model.CardModel
import com.toyou.core.domain.model.CardShortModel
import com.toyou.core.domain.model.ChooseModel
import com.toyou.core.domain.model.PreviewCardModel
import com.toyou.core.domain.model.PreviewChooseModel

data class MyCardUiState(
    val cards: List<CardModel> = emptyList(),
    val shortCards: List<CardShortModel> = emptyList(),
    val previewCards: List<PreviewCardModel> = emptyList(),
    val chooseCards: List<ChooseModel> = emptyList(),
    val previewChoose: List<PreviewChooseModel> = emptyList(),
    val exposure: Boolean = false,
    val answer: String = "",
    val cardId: Int = 0,
    val date: String = "",
    val emotion: String = "",
    val receiver: String = "",
    val isLoading: Boolean = false
) : UiState

sealed interface MyCardEvent : UiEvent {
    data class ShowError(val message: String) : MyCardEvent
}

sealed interface MyCardAction : UiAction {
    data class LoadCardDetail(val id: Long) : MyCardAction
    data class SetCardId(val cardId: Int) : MyCardAction
    data class SetAnswer(val answer: String) : MyCardAction
    data object ToggleExposure : MyCardAction
    data object ClearAllData : MyCardAction
    data object ClearAll : MyCardAction
}
