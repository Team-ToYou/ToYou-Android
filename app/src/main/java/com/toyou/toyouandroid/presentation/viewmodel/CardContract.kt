package com.toyou.toyouandroid.presentation.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.CardShortModel
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.PreviewChooseModel

data class CardUiState(
    val cards: List<CardModel> = emptyList(),
    val shortCards: List<CardShortModel> = emptyList(),
    val previewCards: List<PreviewCardModel> = emptyList(),
    val chooseCards: List<ChooseModel> = emptyList(),
    val previewChoose: List<PreviewChooseModel> = emptyList(),
    val exposure: Boolean = true,
    val answer: String = "",
    val cardId: Int = 0,
    val isAllAnswersFilled: Boolean = false,
    val countSelection: Int = 0,
    val lockDisabled: Boolean = false,
    val date: String = "",
    val emotion: String = "",
    val receiver: String = ""
) : UiState

sealed interface CardEvent : UiEvent {
    data object SendDataSuccess : CardEvent
    data object SendDataFailed : CardEvent
    data object PatchCardSuccess : CardEvent
    data object PatchCardFailed : CardEvent
    data class ShowError(val message: String) : CardEvent
}

sealed interface CardAction : UiAction {
    data class SetCardId(val cardId: Int) : CardAction
    data class DisableLock(val lock: Boolean) : CardAction
    data class SetCardCount(val count: Int, val count2: Int, val count3: Int) : CardAction
    data class UpdateCardInputStatus(val index: Int, val isFilled: Boolean) : CardAction
    data class UpdateCardInputStatusLong(val index: Int, val isFilled: Boolean) : CardAction
    data class UpdateCardInputStatusChoose(val index: Int, val isFilled: Boolean) : CardAction
    data class SendData(val previewCardModels: List<PreviewCardModel>, val exposure: Boolean) : CardAction
    data object GetAllData : CardAction
    data class UpdateButtonState(val position: Int, val isSelected: Boolean) : CardAction
    data class UpdateShortButtonState(val position: Int, val isSelected: Boolean) : CardAction
    data class UpdateChooseButton(val position: Int, val isSelected: Boolean) : CardAction
    data object UpdateAllPreviews : CardAction
    data object ClearAllData : CardAction
    data object ClearAll : CardAction
    data class PatchCard(val previewCardModels: List<PreviewCardModel>, val exposure: Boolean, val id: Int) : CardAction
    data class CountSelect(val selection: Boolean) : CardAction
    data object ResetSelect : CardAction
    data object ToggleLock : CardAction
    data class UpdateAnswer(val answer: String) : CardAction
}
