package com.toyou.toyouandroid.presentation.fragment.mypage

data class MypageUiState(
    val userId: Int? = null,
    val nickname: String? = null,
    val status: String? = null,
    val friendNum: Int? = null,
    val isLoading: Boolean = false
)