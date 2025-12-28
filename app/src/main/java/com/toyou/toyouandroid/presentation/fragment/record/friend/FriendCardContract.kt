package com.toyou.toyouandroid.presentation.fragment.record.friend

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.CardShortModel
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.model.PreviewChooseModel

data class FriendCardUiState(
    val cards: List<CardModel> = emptyList(),
    val shortCards: List<CardShortModel> = emptyList(),
    val previewCards: List<PreviewCardModel> = emptyList(),
    val chooseCards: List<ChooseModel> = emptyList(),
    val previewChoose: List<PreviewChooseModel> = emptyList(),
    val exposure: Boolean = false,
    val answer: String = "",
    val cardId: Int = 0,
    val isAllAnswersFilled: Boolean = false,
    val date: String = "",
    val emotion: String = "",
    val receiver: String = ""
) : UiState

sealed interface FriendCardEvent : UiEvent {
    data class ShowError(val message: String) : FriendCardEvent
}

sealed interface FriendCardAction : UiAction {
    data class GetCardDetail(val id: Long) : FriendCardAction
    data class SetCardId(val cardId: Int) : FriendCardAction
    data class UpdateAnswer(val answer: String) : FriendCardAction
    data object ClearAllData : FriendCardAction
    data object ClearAll : FriendCardAction
}
