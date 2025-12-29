package com.toyou.feature.home.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

data class UserUiState(
    val cardId: Int? = null,
    val emotion: String? = null,
    val nickname: String = "",
    val cardNum: Int = 0,
    val uncheckedAlarm: Boolean = false,
    val isLoading: Boolean = false
) : UiState

sealed interface UserEvent : UiEvent {
    data class ShowError(val message: String) : UserEvent
    data object TokenExpired : UserEvent
}

sealed interface UserAction : UiAction {
    data object LoadHomeEntry : UserAction
    data class UpdateCardId(val cardId: Int?) : UserAction
}
