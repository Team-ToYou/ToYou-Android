package com.toyou.toyouandroid.presentation.fragment.onboarding

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.R

data class SignupStatusUiState(
    val selectedButtonId: Int? = null,
    val isNextButtonEnabled: Boolean = false,
    val nextButtonTextColor: Int = 0xFFA6A6A6.toInt(),
    val nextButtonBackground: Int = R.drawable.next_button,
    val status: String = ""
) : UiState

sealed interface SignupStatusEvent : UiEvent

sealed interface SignupStatusAction : UiAction {
    data class ButtonClicked(val buttonId: Int) : SignupStatusAction
}
