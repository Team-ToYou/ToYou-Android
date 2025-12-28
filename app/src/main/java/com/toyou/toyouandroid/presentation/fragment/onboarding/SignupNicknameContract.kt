package com.toyou.toyouandroid.presentation.fragment.onboarding

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.presentation.fragment.mypage.DuplicateCheckMessageType

data class SignupNicknameUiState(
    val title: String = "회원가입",
    val textCount: String = "0/15",
    val nickname: String = "",
    val isDuplicateCheckEnabled: Boolean = false,
    val isNextButtonEnabled: Boolean = false,
    val duplicateCheckMessage: String = "중복된 닉네임인지 확인해주세요",
    val isNicknameValid: Boolean = false,
    val duplicateCheckMessageType: DuplicateCheckMessageType = DuplicateCheckMessageType.CHECK_REQUIRED,
    val duplicateCheckButtonTextColor: Int = 0xFFA6A6A6.toInt(),
    val duplicateCheckButtonBackground: Int = com.toyou.toyouandroid.R.drawable.next_button,
    val duplicateCheckMessageColor: Int = 0xFF000000.toInt(),
    val nextButtonBackground: Int = com.toyou.toyouandroid.R.drawable.next_button,
    val nextButtonTextColor: Int = 0xFFA6A6A6.toInt()
) : UiState

sealed interface SignupNicknameEvent : UiEvent {
    data class ShowError(val message: String) : SignupNicknameEvent
    data object ServerError : SignupNicknameEvent
}

sealed interface SignupNicknameAction : UiAction {
    data class UpdateTextCount(val count: Int) : SignupNicknameAction
    data class SetNickname(val nickname: String) : SignupNicknameAction
    data class UpdateLength15(val length: Int) : SignupNicknameAction
    data object ActivateDuplicateButton : SignupNicknameAction
    data object ResetState : SignupNicknameAction
    data class CheckDuplicate(val userId: Int) : SignupNicknameAction
}
