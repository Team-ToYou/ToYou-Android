package com.toyou.feature.onboarding.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.network.model.onboarding.SignUpRequest
import com.toyou.core.network.api.AuthService
import com.toyou.core.domain.repository.IFCMRepository
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FcmToken
import com.toyou.core.datastore.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage,
    private val errorHandler: ApiErrorHandler,
    private val fcmRepository: IFCMRepository
) : MviViewModel<LoginUiState, LoginEvent, LoginAction>(
    initialState = LoginUiState()
) {
    private var isSendingToken = false

    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.SetLoginSuccess -> performSetLoginSuccess(action.value)
            is LoginAction.SetIfTokenExists -> performSetIfTokenExists(action.value)
            is LoginAction.SetInitialization -> performSetInitialization(action.value)
            is LoginAction.CheckIfTokenExists -> performCheckIfTokenExists()
            is LoginAction.KakaoLogin -> performKakaoLogin(action.accessToken)
            is LoginAction.SetOAuthAccessToken -> performSetOAuthAccessToken(action.oAuthAccessToken)
            is LoginAction.SignUp -> performSignUp(action.signUpRequest)
            is LoginAction.ReissueJWT -> performReissueJWT(action.refreshToken)
            is LoginAction.PatchFcm -> performPatchFcm(action.token)
        }
    }

    private fun performSetLoginSuccess(value: Boolean) {
        updateState { copy(loginSuccess = value) }
        Timber.d("Login Success value: $value")
    }

    private fun performSetIfTokenExists(value: Boolean) {
        updateState { copy(checkIfTokenExists = value) }
        Timber.d("checkIfTokenExists: $value")
    }

    private fun performSetInitialization(value: Boolean) {
        updateState { copy(isInitialization = value) }
    }

    private fun performCheckIfTokenExists() {
        viewModelScope.launch {
            val accessToken = tokenStorage.accessTokenFlow.first()
            if (accessToken.isNullOrEmpty()) {
                Timber.d("User Info Not Existed")
                updateState { copy(checkIfTokenExists = false) }
            } else {
                Timber.d("User Info Existed")
                updateState { copy(checkIfTokenExists = true) }
            }
        }
    }

    private fun performKakaoLogin(accessToken: String) {
        viewModelScope.launch {
            Timber.d("Attempting to log in with Kakao token: $accessToken")
            try {
                val response = authService.kakaoLogin(accessToken)
                if (response.isSuccessful) {
                    response.headers()["access_token"]?.let { newAccessToken ->
                        response.headers()["refresh_token"]?.let { newRefreshToken ->
                            Timber.d("Tokens received from server")
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                            val fcmToken = tokenStorage.fcmTokenFlow.first().toString()
                            sendTokenToServer(fcmToken)
                            updateState { copy(loginSuccess = true) }
                            sendEvent(LoginEvent.LoginSucceeded)
                            Timber.i("Tokens saved successfully")
                        } ?: Timber.e("Refresh token missing in response headers")
                    } ?: Timber.e("Access token missing in response headers")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                    updateState { copy(loginSuccess = false) }
                    sendEvent(LoginEvent.LoginFailed)
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                Timber.e("Network Failure: $errorMessage")
                updateState { copy(loginSuccess = false) }
                sendEvent(LoginEvent.LoginFailed)
            }
        }
    }

    private fun performSetOAuthAccessToken(oAuthAccessToken: String) {
        updateState { copy(oAuthAccessToken = oAuthAccessToken) }
    }

    private fun performSignUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            val accessToken = currentState.oAuthAccessToken
            Timber.d("Attempting to sign up with Kakao token: $accessToken, signUpRequest: $signUpRequest")
            try {
                val response = authService.signUp(accessToken, signUpRequest)
                if (response.isSuccessful) {
                    response.headers()["access_token"]?.let { newAccessToken ->
                        response.headers()["refresh_token"]?.let { newRefreshToken ->
                            Timber.d("Tokens received from server")
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                            val fcmToken = tokenStorage.fcmTokenFlow.first().toString()
                            sendTokenToServer(fcmToken)
                            sendEvent(LoginEvent.SignUpSucceeded)
                            Timber.i("Tokens saved successfully")
                        } ?: Timber.e("Refresh token missing in response headers")
                    } ?: Timber.e("Access token missing in response headers")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                    sendEvent(LoginEvent.SignUpFailed)
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                Timber.e("Network Failure: $errorMessage")
                sendEvent(LoginEvent.SignUpFailed)
            }
        }
    }

    private fun performReissueJWT(refreshToken: String) {
        viewModelScope.launch {
            Timber.d("Attempting to reissueJWT: $refreshToken")
            try {
                val response = authService.reissue(refreshToken)
                if (response.isSuccessful) {
                    response.headers()["access_token"]?.let { newAccessToken ->
                        response.headers()["refresh_token"]?.let { newRefreshToken ->
                            Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                            Timber.i("Tokens saved successfully")
                            updateState { copy(navigationEvent = true) }
                            sendEvent(LoginEvent.ReissueSucceeded)
                        } ?: Timber.e("Refresh token missing in response headers")
                    } ?: Timber.e("Access token missing in response headers")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                    updateState { copy(navigationEvent = false) }
                    sendEvent(LoginEvent.ReissueFailed)
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                Timber.e("Network Failure: $errorMessage")
                updateState { copy(navigationEvent = false) }
                sendEvent(LoginEvent.ReissueFailed)
            }
        }
    }

    private fun sendTokenToServer(token: String, retryCount: Int = 0) {
        val maxRetries = 2

        if (retryCount > maxRetries) {
            Timber.e("sendTokenToServer failed after $maxRetries retries. Aborting.")
            return
        }

        if (isSendingToken) {
            Timber.d("sendTokenToServer is already in progress, skipping this call.")
            return
        }

        isSendingToken = true

        val tokenRequest = FcmToken(token)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val result = fcmRepository.postToken(tokenRequest)) {
                    is DomainResult.Success -> {
                        Timber.tag("sendTokenToServer").d("토큰 전송 성공")
                        tokenStorage.setTokenSent(true)
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleErrorWithRetry(
                            maxRetries = maxRetries,
                            currentRetry = retryCount,
                            error = result,
                            onRetry = { newRetryCount ->
                                sendTokenToServer(token, newRetryCount)
                            },
                            onMaxRetriesExceeded = {
                                isSendingToken = false
                            },
                            tag = "LoginViewModel-sendToken"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("sendTokenToServer").e("Exception occurred: ${e.message}")
                isSendingToken = false
            } finally {
                isSendingToken = false
            }
        }
    }

    private fun performPatchFcm(token: String) {
        val tokenRequest = FcmToken(token)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    fcmRepository.patchToken(tokenRequest)
                }
                when (result) {
                    is DomainResult.Success -> {
                        Timber.tag("patchTokenToServer").d("토큰 전송 성공")
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performPatchFcm(token) },
                            onFailure = { Timber.e("patchFcm API Call Failed - Refresh token failed") },
                            tag = "LoginViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("patchTokenToServer").e("Exception occurred: ${e.message}")
            }
        }
    }

    fun setLoginSuccess(value: Boolean) = onAction(LoginAction.SetLoginSuccess(value))
    fun setIfTokenExists(value: Boolean) = onAction(LoginAction.SetIfTokenExists(value))
    fun setInitialization(value: Boolean) = onAction(LoginAction.SetInitialization(value))
    fun checkIfTokenExists() = onAction(LoginAction.CheckIfTokenExists)
    fun kakaoLogin(accessToken: String) = onAction(LoginAction.KakaoLogin(accessToken))
    fun setOAuthAccessToken(oAuthAccessToken: String) = onAction(LoginAction.SetOAuthAccessToken(oAuthAccessToken))
    fun signUp(signUpRequest: SignUpRequest) = onAction(LoginAction.SignUp(signUpRequest))
    fun reissueJWT(refreshToken: String) = onAction(LoginAction.ReissueJWT(refreshToken))
    fun patchFcm(token: String) = onAction(LoginAction.PatchFcm(token))
}
