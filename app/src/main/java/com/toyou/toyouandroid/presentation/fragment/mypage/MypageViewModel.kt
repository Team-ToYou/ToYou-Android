package com.toyou.toyouandroid.presentation.fragment.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.presentation.fragment.onboarding.data.dto.response.SignUpResponse
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MypageViewModel(private val authService: AuthService, private val tokenStorage: TokenStorage) : ViewModel() {

    fun kakaoLogout() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            Timber.d("Attempting to logout in with refresh token: $refreshToken")

            authService.logout(refreshToken).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        Timber.i("Logout successfully")
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Timber.e("API Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    val errorMessage = t.message ?: "Unknown error"
                    Timber.e("Network Failure: $errorMessage")
                }
            })
        }
    }

    fun kakaoSignOut() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            Timber.d("Attempting to signout in with refresh token: $refreshToken")

            authService.signOut(refreshToken).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        Timber.i("SignOut successfully")
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Timber.e("API Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    val errorMessage = t.message ?: "Unknown error"
                    Timber.e("Network Failure: $errorMessage")
                }
            })
        }
    }
}