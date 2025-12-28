package com.toyou.toyouandroid.presentation.fragment.onboarding

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.R

data class SignupAgreeUiState(
    val imageStates: List<Boolean> = listOf(false, false, false, false),
    val isNextButtonEnabled: Boolean = false,
    val nextButtonTextColor: Int = 0xFFA6A6A6.toInt(),
    val nextButtonBackground: Int = R.drawable.next_button
) : UiState

sealed interface SignupAgreeEvent : UiEvent

sealed interface SignupAgreeAction : UiAction {
    data class ImageClicked(val index: Int, val newImageResId: Int) : SignupAgreeAction
}
