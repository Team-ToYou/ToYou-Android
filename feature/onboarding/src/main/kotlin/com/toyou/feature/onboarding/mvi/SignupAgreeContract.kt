package com.toyou.feature.onboarding.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

data class SignupAgreeUiState(
    val imageStates: List<Boolean> = listOf(false, false, false, false),
    val isNextButtonEnabled: Boolean = false,
    val nextButtonTextColor: Int = 0xFFA6A6A6.toInt(),
    val nextButtonBackground: Int = 0
) : UiState

sealed interface SignupAgreeEvent : UiEvent

sealed interface SignupAgreeAction : UiAction {
    data class ImageClicked(val index: Int, val newImageResId: Int) : SignupAgreeAction
}
