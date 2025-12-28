package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.data.onboarding.dto.request.SignUpRequest
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.fcm.domain.IFCMRepository
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.core.datastore.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage,
    private val tokenManager: TokenManager,
    private val fcmRepository: IFCMRepository
) : MviViewModel<LoginUiState, LoginEvent, LoginAction>(
    initialState = LoginUiState()
) {
    private var isSendingToken = false

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _checkIfTokenExists = MutableLiveData<Boolean>()
    val checkIfTokenExists: LiveData<Boolean> get() = _checkIfTokenExists

    private val _isInitialization = MutableLiveData<Boolean>()
    val isInitialization: LiveData<Boolean> get() = _isInitialization

    private val _oAuthAccessToken = MutableLiveData<String>()
    val oAuthAccessToken: LiveData<String> get() = _oAuthAccessToken

    private val _navigationEvent = MutableLiveData<Boolean>()
    val navigationEvent: LiveData<Boolean> get() = _navigationEvent

    init {
        state.onEach { uiState ->
            _loginSuccess.value = uiState.loginSuccess
            _checkIfTokenExists.value = uiState.checkIfTokenExists
            _isInitialization.value = uiState.isInitialization
            _oAuthAccessToken.value = uiState.oAuthAccessToken
            uiState.navigationEvent?.let { _navigationEvent.value = it }
        }.launchIn(viewModelScope)
    }

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
        tokenStorage.let { storage ->
            val accessToken = storage.getAccessToken()
            if (accessToken == "") {
                Timber.d("User Info Not Existed")
                updateState { copy(checkIfTokenExists = false) }
            } else {
                Timber.d("User Info Existed: $accessToken")
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
                            Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                            sendTokenToServer(tokenStorage.getFcmToken().toString())
                            AuthNetworkModule.setAccessToken(newAccessToken)
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
                            Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                            sendTokenToServer(tokenStorage.getFcmToken().toString())
                            AuthNetworkModule.setAccessToken(newAccessToken)
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
                            AuthNetworkModule.setAccessToken(newAccessToken)
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)
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

        val tokenRequest = Token(token)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = fcmRepository.postToken(tokenRequest)
                if (response.isSuccess) {
                    Timber.tag("sendTokenToServer").d("토큰 전송 성공")
                    tokenStorage.setTokenSent(true)
                } else {
                    Timber.tag("sendTokenToServer").d("토큰 전송 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = {
                            Timber.d("Token refreshed successfully. Retrying sendTokenToServer.")
                            sendTokenToServer(token, retryCount + 1)
                        },
                        onFailure = {
                            Timber.e("sendTokenToServer API Call Failed - Refresh token failed.")
                            isSendingToken = false
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("sendTokenToServer").e("Exception occurred: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = {
                        Timber.d("Token refreshed successfully. Retrying sendTokenToServer.")
                        sendTokenToServer(token, retryCount + 1)
                    },
                    onFailure = {
                        Timber.e("sendTokenToServer API Call Failed - Refresh token failed.")
                        isSendingToken = false
                    }
                )
            } finally {
                isSendingToken = false
            }
        }
    }

    private fun performPatchFcm(token: String) {
        val tokenRequest = Token(token)
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    fcmRepository.patchToken(tokenRequest)
                }
                if (response.isSuccess) {
                    Timber.tag("patchTokenToServer").d("토큰 전송 성공")
                } else {
                    Timber.tag("patchTokenToServer").d("토큰 전송 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { performPatchFcm(token) },
                        onFailure = { Timber.e("patchFcm API Call Failed - Refresh token failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("patchTokenToServer").e("Exception occurred: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { performPatchFcm(token) },
                    onFailure = { Timber.e("patchFcm API Call Failed - Refresh token failed") }
                )
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
