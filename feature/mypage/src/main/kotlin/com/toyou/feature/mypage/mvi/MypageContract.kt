package com.toyou.feature.mypage.viewmodel

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState

data class MypageUiState(
    val userId: Int? = null,
    val nickname: String? = null,
    val status: String? = null,
    val friendNum: Int? = null,
    val isLoading: Boolean = false
) : UiState

sealed interface MypageEvent : UiEvent {
    data class LogoutResult(val success: Boolean) : MypageEvent
    data class SignOutResult(val success: Boolean) : MypageEvent
}

sealed interface MypageAction : UiAction {
    data object LoadMypage : MypageAction
    data object Logout : MypageAction
    data object SignOut : MypageAction
}
