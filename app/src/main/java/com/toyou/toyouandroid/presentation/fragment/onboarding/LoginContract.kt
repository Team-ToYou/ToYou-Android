package com.toyou.toyouandroid.presentation.fragment.onboarding

import com.toyou.core.common.mvi.UiAction
import com.toyou.core.common.mvi.UiEvent
import com.toyou.core.common.mvi.UiState
import com.toyou.toyouandroid.data.onboarding.dto.request.SignUpRequest

data class LoginUiState(
    val loginSuccess: Boolean = false,
    val checkIfTokenExists: Boolean = false,
    val isInitialization: Boolean = false,
    val oAuthAccessToken: String = "",
    val navigationEvent: Boolean? = null
) : UiState

sealed interface LoginEvent : UiEvent {
    data object LoginSucceeded : LoginEvent
    data object LoginFailed : LoginEvent
    data object SignUpSucceeded : LoginEvent
    data object SignUpFailed : LoginEvent
    data object ReissueSucceeded : LoginEvent
    data object ReissueFailed : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}

sealed interface LoginAction : UiAction {
    data class SetLoginSuccess(val value: Boolean) : LoginAction
    data class SetIfTokenExists(val value: Boolean) : LoginAction
    data class SetInitialization(val value: Boolean) : LoginAction
    data object CheckIfTokenExists : LoginAction
    data class KakaoLogin(val accessToken: String) : LoginAction
    data class SetOAuthAccessToken(val oAuthAccessToken: String) : LoginAction
    data class SignUp(val signUpRequest: SignUpRequest) : LoginAction
    data class ReissueJWT(val refreshToken: String) : LoginAction
    data class PatchFcm(val token: String) : LoginAction
}
