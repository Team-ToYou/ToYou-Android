package com.toyou.toyouandroid.presentation.fragment.mypage

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
)

enum class StatusType(val value: String) {
    SCHOOL("SCHOOL"),
    COLLEGE("COLLEGE"),
    OFFICE("OFFICE"),
    ETC("ETC")
}