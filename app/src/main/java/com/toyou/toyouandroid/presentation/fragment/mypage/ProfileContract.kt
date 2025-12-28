package com.toyou.toyouandroid.presentation.fragment.mypage

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

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

enum class StatusType(val value: String) {
    SCHOOL("SCHOOL"),
    COLLEGE("COLLEGE"),
    OFFICE("OFFICE"),
    ETC("ETC")
}

enum class DuplicateCheckMessageType(val message: String) {
    CHECK_REQUIRED("중복된 닉네임인지 확인해주세요"),
    LENGTH_EXCEEDED("15자 이내로 입력해주세요."),
    AVAILABLE("사용 가능한 닉네임입니다."),
    ALREADY_IN_USE("이미 사용 중인 닉네임입니다."),
    ALREADY_IN_USE_SAME("이미 사용 중인 닉네임입니다."),
    CHECK_FAILED("닉네임 확인에 실패했습니다."),
    UPDATE_FAILED("닉네임 변경에 실패했습니다."),
    SERVER_ERROR("서버에 연결할 수 없습니다.")
}
