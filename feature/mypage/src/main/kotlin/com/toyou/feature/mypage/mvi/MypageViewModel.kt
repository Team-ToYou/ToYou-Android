package com.toyou.feature.mypage.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.network.api.MypageService
import com.toyou.core.network.api.AuthService
import com.toyou.core.datastore.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val authService: AuthService,
    private val mypageService: MypageService,
    private val tokenStorage: TokenStorage,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<MypageUiState, MypageEvent, MypageAction>(MypageUiState()) {

    override fun handleAction(action: MypageAction) {
        when (action) {
            is MypageAction.LoadMypage -> performUpdateMypage()
            is MypageAction.Logout -> performKakaoLogout()
            is MypageAction.SignOut -> performKakaoSignOut()
        }
    }

    private fun performKakaoLogout() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.refreshTokenFlow.first().toString()
            val accessToken = tokenStorage.accessTokenFlow.first().toString()
            Timber.d("Attempting to logout")
            Timber.d("Tokens retrieved")

            try {
                val response = authService.logout(refreshToken)
                if (response.isSuccessful) {
                    Timber.i("Logout successfully")
                    sendEvent(MypageEvent.LogoutResult(true))
                    tokenStorage.clearTokens()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error: ${response.message()}"
                    Timber.e("API Error: $errorMessage")

                    if (errorHandler.handleUnauthorized(
                        responseCode = response.code(),
                        onRetry = { performKakaoLogout() },
                        onFailure = {
                            sendEvent(MypageEvent.LogoutResult(false))
                        },
                        tag = "MypageViewModel"
                    )) {
                        return@launch
                    }
                    sendEvent(MypageEvent.LogoutResult(false))
                }
            } catch (e: Exception) {
                Timber.e("Network Failure: ${e.message}")
                sendEvent(MypageEvent.LogoutResult(false))
            }
        }
    }

    private fun performKakaoSignOut() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.refreshTokenFlow.first().toString()
            val accessToken = tokenStorage.accessTokenFlow.first().toString()
            Timber.d("Attempting to signout")
            Timber.d("Tokens retrieved")

            try {
                val response = authService.signOut(refreshToken)
                if (response.isSuccessful) {
                    Timber.i("SignOut successfully")
                    sendEvent(MypageEvent.SignOutResult(true))
                    tokenStorage.clearTokens()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                    if (errorHandler.handleUnauthorized(
                        responseCode = response.code(),
                        onRetry = { performKakaoSignOut() },
                        onFailure = {
                            sendEvent(MypageEvent.SignOutResult(false))
                        },
                        tag = "MypageViewModel"
                    )) {
                        return@launch
                    }
                    sendEvent(MypageEvent.SignOutResult(false))
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
                    if (errorHandler.handleUnauthorized(
                        responseCode = response.code(),
                        onRetry = { performUpdateMypage() },
                        onFailure = {
                            updateState { copy(isLoading = false) }
                        },
                        tag = "MypageViewModel"
                    )) {
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error occurred during API call")
                updateState { copy(isLoading = false) }
            }
        }
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
