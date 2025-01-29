package com.toyou.toyouandroid.presentation.fragment.onboarding

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
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LoginViewModel(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage,
    private val tokenManager: TokenManager,
    private val fcmRepository: FCMRepository

) : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    //private val fcmRepository by lazy { FCMRepository(tokenManager) }

    fun setLoginSuccess(value: Boolean) {
        _loginSuccess.value = value
        Timber.d("Login Success value: $value")
    }

    private val _checkIfTokenExists = MutableLiveData<Boolean>()
    val checkIfTokenExists: LiveData<Boolean> get() = _checkIfTokenExists

    private val _isInitialization = MutableLiveData<Boolean>()
    val isInitialization: LiveData<Boolean> get() = _isInitialization

    fun setIfTokenExists(value: Boolean) {
        _checkIfTokenExists.value = value
        Timber.d("checkIfTokenExists: $value")
    }

    fun setInitialization(value: Boolean) {
        _isInitialization.value = value
    }

    fun checkIfTokenExists() {
        tokenStorage.let { storage ->
            val accessToken = storage.getAccessToken()
            if (accessToken == "") {
                // 액세스 토큰이 없으면 회원가입 동의 화면으로 이동
                Timber.d("User Info Not Existed")
                _checkIfTokenExists.value = false
            }else {
                Timber.d("User Info Existed: $accessToken")
                _checkIfTokenExists.value = true
            }
        }
    }

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
                                Timber.i("Tokens saved successfully")

                                // 인증 네트워크 모듈에 access token 저장
                                AuthNetworkModule.setAccessToken(newAccessToken)

                                tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                                //sendTokenToServer(tokenStorage.getFcmToken().toString())
                                _navigationEvent.value = true  // 성공하면 홈 화면으로 이동

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
    private var isSendingToken = false // 호출 여부를 추적하는 플래그

    private fun sendTokenToServer(token: String, retryCount: Int = 0) {

        val maxRetries = 2

        // 재시도 횟수가 maxRetries를 초과하면 더 이상 호출하지 않음
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = fcmRepository.postToken(tokenRequest)
                if (response.isSuccess) {
                    Timber.tag("sendTokenToServer").d("토큰 전송 성공")
                    tokenStorage.setTokenSent(true) // 전송 성공 상태 저장
                } else {
                    Timber.tag("sendTokenToServer").d("토큰 전송 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = {
                            Timber.d("Token refreshed successfully. Retrying sendTokenToServer.")
                            sendTokenToServer(token, retryCount + 1) // 재시도
                        },
                        onFailure = {
                            Timber.e("sendTokenToServer API Call Failed - Refresh token failed.")
                            isSendingToken = false // 플래그 초기화
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("sendTokenToServer").e("Exception occurred: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = {
                        Timber.d("Token refreshed successfully. Retrying sendTokenToServer.")
                        sendTokenToServer(token, retryCount + 1) // 재시도
                    },
                    onFailure = {
                        Timber.e("sendTokenToServer API Call Failed - Refresh token failed.")
                        isSendingToken = false // 플래그 초기화
                    }
                )
            } finally {
                isSendingToken = false // 호출 완료 후 플래그 초기화
            }
        }
    }

    fun patchFcm(token: String) {
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
                        onSuccess = { patchFcm(token) },
                        onFailure = { Timber.e("patchFcm API Call Failed - Refresh token failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("patchTokenToServer").e("Exception occurred: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { patchFcm(token) },
                    onFailure = { Timber.e("patchFcm API Call Failed - Refresh token failed") }
                )
            }
        }
    }

}