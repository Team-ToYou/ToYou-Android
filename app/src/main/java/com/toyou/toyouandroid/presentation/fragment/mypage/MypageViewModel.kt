package com.toyou.toyouandroid.presentation.fragment.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.mypage.service.MypageService
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
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
) : ViewModel() {

    private val _uiState = MutableLiveData(MypageUiState())
    val uiState: LiveData<MypageUiState> get() = _uiState

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private val _signOutSuccess = MutableLiveData<Boolean>()
    val signOutSuccess: LiveData<Boolean> get() = _signOutSuccess

    fun setLogoutSuccess(value: Boolean) {
        _logoutSuccess.value = value
    }

    fun setSignOutSuccess(value: Boolean) {
        _signOutSuccess.value = value
    }

    fun kakaoLogout() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            val accessToken = tokenStorage.getAccessToken().toString()
            Timber.d("Attempting to logout with refresh token: $refreshToken")
            Timber.d("accessToken: $accessToken")

            try {
                val response = authService.logoutSuspend(refreshToken)
                if (response.isSuccessful) {
                    Timber.i("Logout successfully")
                    _logoutSuccess.value = true
                    tokenStorage.clearTokens()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error: ${response.message()}"
                    Timber.e("API Error: $errorMessage")
                    
                    if (response.code() == 401) {
                        tokenManager.refreshToken(
                            onSuccess = { kakaoLogout() },
                            onFailure = { 
                                Timber.e("Failed to refresh token and kakao logout")
                                _logoutSuccess.value = false
                            }
                        )
                    } else {
                        _logoutSuccess.value = false
                    }
                }
            } catch (e: Exception) {
                Timber.e("Network Failure: ${e.message}")
                _logoutSuccess.value = false
            }
        }
    }

    fun kakaoSignOut() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            val accessToken = tokenStorage.getAccessToken().toString()
            Timber.d("Attempting to signout with refresh token: $refreshToken")
            Timber.d("accessToken: $accessToken")

            try {
                val response = authService.signOutSuspend(refreshToken)
                if (response.isSuccessful) {
                    Timber.i("SignOut successfully")
                    _signOutSuccess.value = true
                    tokenStorage.clearTokens()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                    tokenManager.refreshToken(
                        onSuccess = { kakaoSignOut() },
                        onFailure = { 
                            Timber.e("Failed to refresh token and kakao signout")
                            _signOutSuccess.value = false
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.e("Network Failure: ${e.message}")
                _signOutSuccess.value = false
            }
        }
    }

    fun updateMypage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)
            try {
                val response = mypageService.getMypageSuspend()
                if (response.isSuccessful) {
                    response.body()?.result?.let { result ->
                        _uiState.value = MypageUiState(
                            userId = result.userId,
                            nickname = result.nickname,
                            status = result.status,
                            friendNum = result.friendNum,
                            isLoading = false
                        )
                        Timber.tag("updateMypage").d("Mypage updated: $result")
                    }
                } else {
                    Timber.tag("API Error").e("Failed to update Mypage. Code: ${response.code()}, Message: ${response.message()}")
                    _uiState.value = _uiState.value?.copy(isLoading = false)
                    tokenManager.refreshToken(
                        onSuccess = { updateMypage() },
                        onFailure = { 
                            Timber.e("Failed to refresh token and get mypage")
                            _uiState.value = _uiState.value?.copy(isLoading = false)
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error occurred during API call")
                _uiState.value = _uiState.value?.copy(isLoading = false)
            }
        }
    }
}