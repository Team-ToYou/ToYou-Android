package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.data.onboarding.dto.request.SignUpRequest
import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LoginViewModel(private val authService: AuthService, private val tokenStorage: TokenStorage) : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess
    private val fcmRepository by lazy { FCMRepository(tokenStorage) }


    fun kakaoLogin(accessToken: String) {
        viewModelScope.launch {
            Timber.d("Attempting to log in with Kakao token: $accessToken")

            authService.kakaoLogin(accessToken).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        response.headers()["access_token"]?.let { newAccessToken ->
                            response.headers()["refresh_token"]?.let { newRefreshToken ->
                                Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")

                                // 암호화된 토큰 저장소에 저장
                                tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                                sendTokenToServer(tokenStorage.getFcmToken().toString())

                                // 인증 네트워크 모듈에 access token 저장
                                AuthNetworkModule.setAccessToken(newAccessToken)

                                _loginSuccess.postValue(true)

                                Timber.i("Tokens saved successfully")
                            } ?: Timber.e("Refresh token missing in response headers")
                        } ?: Timber.e("Access token missing in response headers")
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Timber.e("API Error: $errorMessage")

                        _loginSuccess.postValue(false)
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    val errorMessage = t.message ?: "Unknown error"
                    Timber.e("Network Failure: $errorMessage")

                    _loginSuccess.postValue(false)
                }
            })
        }
    }

    private val _oAuthAccessToken = MutableLiveData<String>()
    val oAuthAccessToken: LiveData<String> get() = _oAuthAccessToken

    fun setOAuthAccessToken(oAuthAccessToken: String) {
        _oAuthAccessToken.value = oAuthAccessToken
    }

    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            val accessToken = _oAuthAccessToken.value ?: ""
            Timber.d("Attempting to sign up with Kakao token: $accessToken, signUpRequest: $signUpRequest")

            authService.signUp(accessToken, signUpRequest).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        response.headers()["access_token"]?.let { newAccessToken ->
                            response.headers()["refresh_token"]?.let { newRefreshToken ->
                                Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")

                                // 암호화된 토큰 저장소에 저장
                                tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                                sendTokenToServer(tokenStorage.getFcmToken().toString())

                                // 인증 네트워크 모듈에 access token 저장
                                AuthNetworkModule.setAccessToken(newAccessToken)

                                Timber.i("Tokens saved successfully")
                            } ?: Timber.e("Refresh token missing in response headers")
                        } ?: Timber.e("Access token missing in response headers")
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

    private val _navigationEvent = MutableLiveData<Boolean>()
    val navigationEvent: LiveData<Boolean> get() = _navigationEvent

    fun reissueJWT(refreshToken: String) {
        viewModelScope.launch {
            Timber.d("Attempting to reissueJWT: $refreshToken")

            authService.reissue(refreshToken).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        response.headers()["access_token"]?.let { newAccessToken ->
                            response.headers()["refresh_token"]?.let { newRefreshToken ->
                                Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")

                                // 암호화된 토큰 저장소에 저장
                                tokenStorage.saveTokens(newAccessToken, newRefreshToken)

                                // 인증 네트워크 모듈에 access token 저장
                                AuthNetworkModule.setAccessToken(newAccessToken)

                                tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                                _navigationEvent.value = true  // 성공하면 홈 화면으로 이동

                                Timber.i("Tokens saved successfully")
                            } ?: Timber.e("Refresh token missing in response headers")
                        } ?: Timber.e("Access token missing in response headers")
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Timber.e("API Error: $errorMessage")
                        _navigationEvent.value = false  // 성공하면 로그인 화면으로 이동
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    val errorMessage = t.message ?: "Unknown error"
                    Timber.e("Network Failure: $errorMessage")
                    _navigationEvent.value = false  // 성공하면 로그인 화면으로 이동
                }
            })
        }
    }
    private fun sendTokenToServer(token: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenRequest = Token(token)
                fcmRepository.postToken(tokenRequest)
                Log.d("sendTokenToServer", "성공")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("sendTokenToServer", "토큰 전송 실패: ${e.message}")
            }
        }

    }

    fun patchFcm(token : String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenRequest = Token(token)
                fcmRepository.patchToken(tokenRequest)
                Log.d("patchTokenToServer", "성공")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("patchTokenToServer", "토큰 전송 실패: ${e.message}")
            }
        }
    }
}