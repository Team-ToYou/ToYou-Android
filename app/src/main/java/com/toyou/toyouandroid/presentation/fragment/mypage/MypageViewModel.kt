package com.toyou.toyouandroid.presentation.fragment.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.data.mypage.service.MypageService
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.core.datastore.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val authService: AuthService,
    private val mypageService: MypageService,
    private val tokenStorage: TokenStorage,
    private val tokenManager: TokenManager
) : MviViewModel<MypageUiState, MypageEvent, MypageAction>(MypageUiState()) {

    private val _uiStateLiveData = MutableLiveData(MypageUiState())
    val uiState: LiveData<MypageUiState> get() = _uiStateLiveData

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private val _signOutSuccess = MutableLiveData<Boolean>()
    val signOutSuccess: LiveData<Boolean> get() = _signOutSuccess

    init {
        viewModelScope.launch {
            state.collect { newState ->
                _uiStateLiveData.value = newState
            }
        }
        viewModelScope.launch {
            event.collect { event ->
                when (event) {
                    is MypageEvent.LogoutResult -> _logoutSuccess.value = event.success
                    is MypageEvent.SignOutResult -> _signOutSuccess.value = event.success
                }
            }
        }
    }

    override fun handleAction(action: MypageAction) {
        when (action) {
            is MypageAction.LoadMypage -> performUpdateMypage()
            is MypageAction.Logout -> performKakaoLogout()
            is MypageAction.SignOut -> performKakaoSignOut()
        }
    }

    private fun performKakaoLogout() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            val accessToken = tokenStorage.getAccessToken().toString()
            Timber.d("Attempting to logout with refresh token: $refreshToken")
            Timber.d("accessToken: $accessToken")

            try {
                val response = authService.logout(refreshToken)
                if (response.isSuccessful) {
                    Timber.i("Logout successfully")
                    sendEvent(MypageEvent.LogoutResult(true))
                    tokenStorage.clearTokens()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error: ${response.message()}"
                    Timber.e("API Error: $errorMessage")

                    if (response.code() == 401) {
                        tokenManager.refreshToken(
                            onSuccess = { performKakaoLogout() },
                            onFailure = {
                                Timber.e("Failed to refresh token and kakao logout")
                                sendEvent(MypageEvent.LogoutResult(false))
                            }
                        )
                    } else {
                        sendEvent(MypageEvent.LogoutResult(false))
                    }
                }
            } catch (e: Exception) {
                Timber.e("Network Failure: ${e.message}")
                sendEvent(MypageEvent.LogoutResult(false))
            }
        }
    }

    private fun performKakaoSignOut() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            val accessToken = tokenStorage.getAccessToken().toString()
            Timber.d("Attempting to signout with refresh token: $refreshToken")
            Timber.d("accessToken: $accessToken")

            try {
                val response = authService.signOut(refreshToken)
                if (response.isSuccessful) {
                    Timber.i("SignOut successfully")
                    sendEvent(MypageEvent.SignOutResult(true))
                    tokenStorage.clearTokens()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                    tokenManager.refreshToken(
                        onSuccess = { performKakaoSignOut() },
                        onFailure = {
                            Timber.e("Failed to refresh token and kakao signout")
                            sendEvent(MypageEvent.SignOutResult(false))
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.e("Network Failure: ${e.message}")
                sendEvent(MypageEvent.SignOutResult(false))
            }
        }
    }

    private fun performUpdateMypage() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val response = mypageService.getMypage()
                if (response.isSuccessful) {
                    response.body()?.result?.let { result ->
                        updateState {
                            copy(
                                userId = result.userId,
                                nickname = result.nickname,
                                status = result.status,
                                friendNum = result.friendNum,
                                isLoading = false
                            )
                        }
                        Timber.tag("updateMypage").d("Mypage updated: $result")
                    }
                } else {
                    Timber.tag("API Error").e("Failed to update Mypage. Code: ${response.code()}, Message: ${response.message()}")
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { performUpdateMypage() },
                        onFailure = {
                            Timber.e("Failed to refresh token and get mypage")
                            updateState { copy(isLoading = false) }
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error occurred during API call")
                updateState { copy(isLoading = false) }
            }
        }
    }

    fun setLogoutSuccess(value: Boolean) {
        _logoutSuccess.value = value
    }

    fun setSignOutSuccess(value: Boolean) {
        _signOutSuccess.value = value
    }

    fun kakaoLogout() {
        onAction(MypageAction.Logout)
    }

    fun kakaoSignOut() {
        onAction(MypageAction.SignOut)
    }

    fun updateMypage() {
        onAction(MypageAction.LoadMypage)
    }
}
