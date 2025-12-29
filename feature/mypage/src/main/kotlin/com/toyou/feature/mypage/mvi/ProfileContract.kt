package com.toyou.feature.mypage.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.core.domain.model.DuplicateCheckMessageType
import com.toyou.core.domain.model.StatusType

data class ProfileUiState(
    val title: String = "회원가입",
    val textCount: String = "0/15",
    val nickname: String = "",
    val status: String = "",
    val isDuplicateCheckEnabled: Boolean = false,
    val isNextButtonEnabled: Boolean = false,
    val duplicateCheckMessage: String = "중복된 닉네임인지 확인해주세요",
    val isNicknameValid: Boolean = false,
    val selectedStatusType: StatusType? = null
) : UiState

sealed interface ProfileEvent : UiEvent {
    data object NicknameChangedSuccess : ProfileEvent
    data class DuplicateCheckMessageChanged(val type: DuplicateCheckMessageType) : ProfileEvent
}

sealed interface ProfileAction : UiAction {
    data class UpdateTextCount(val count: Int) : ProfileAction
    data class SetNickname(val nickname: String) : ProfileAction
    data class UpdateLength15(val length: Int) : ProfileAction
    data object DuplicateBtnActivate : ProfileAction
    data object ResetNicknameEditState : ProfileAction
    data class CheckDuplicate(val userNickname: String, val userId: Int) : ProfileAction
    data object ChangeNickname : ProfileAction
    data object ChangeStatus : ProfileAction
    data class OnStatusButtonClicked(val statusType: StatusType) : ProfileAction
}
