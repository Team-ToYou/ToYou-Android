package com.toyou.feature.onboarding.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.core.domain.model.StatusType

data class SignupStatusUiState(
    val selectedStatusType: StatusType? = null,
    val isNextButtonEnabled: Boolean = false,
    val nextButtonTextColor: Int = 0xFFA6A6A6.toInt(),
    val nextButtonBackground: Int = 0,
    val status: String = "",
    val isLoading: Boolean = false
) : UiState

sealed interface SignupStatusEvent : UiEvent {
    data object SignupSuccess : SignupStatusEvent
    data class SignupError(val message: String) : SignupStatusEvent
}

sealed interface SignupStatusAction : UiAction {
    data class StatusSelected(val statusType: StatusType) : SignupStatusAction
    data class Signup(val nickname: String, val accessToken: String) : SignupStatusAction
}
