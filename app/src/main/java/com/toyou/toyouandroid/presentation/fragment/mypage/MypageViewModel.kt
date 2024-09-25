package com.toyou.toyouandroid.presentation.fragment.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.fragment.mypage.network.MypageResponse
import com.toyou.toyouandroid.presentation.fragment.mypage.network.MypageService
import com.toyou.toyouandroid.presentation.fragment.onboarding.data.dto.response.SignUpResponse
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MypageViewModel(private val authService: AuthService, private val tokenStorage: TokenStorage) : ViewModel() {

    private val fcmRepository = FCMRepository(tokenStorage)
    private val _retrieveToken = MutableLiveData<List<String>>()
    val retrieveToken : LiveData<List<String>> get() = _retrieveToken

    fun kakaoLogout() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            Timber.d("Attempting to logout in with refresh token: $refreshToken")

            authService.logout(refreshToken).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        Timber.i("Logout successfully")
                        tokenStorage.clearTokens()
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

    private val apiService: MypageService = AuthNetworkModule.getClient().create(MypageService::class.java)

    private val _friendNum = MutableLiveData<Int?>()
    val friendNum: LiveData<Int?> get() = _friendNum

    private val _nickname = MutableLiveData<String?>()
    val nickname: LiveData<String?> get() = _nickname

    private val _status = MutableLiveData<String?>()
    val status: LiveData<String?> get() = _status

    fun updateMypage() {
        val call = apiService.getMypage()

        call.enqueue(object : Callback<MypageResponse> {
            override fun onResponse(
                call: Call<MypageResponse>,
                response: Response<MypageResponse>
            ) {
                if (response.isSuccessful) {
                    val friendNumber = response.body()?.result?.friendNum
                    val nickname = response.body()?.result?.nickname
                    val status = response.body()?.result?.status

                    _friendNum.postValue(friendNumber)
                    _nickname.postValue(nickname)
                    _status.postValue(status)

                    Timber.tag("updateFriendNum").d("FriendNum updated: $friendNumber")
                } else {
                    Timber.tag("API Error").e("Failed to update FriendNum. Code: ${response.code()}, Message: ${response.message()}")
                    Timber.tag("API Error").e("Response Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MypageResponse>, t: Throwable) {
                Timber.tag("API Failure").e(t, "Error occurred during API call")
            }
        })
    }

    fun retrieveTokenFromServer(name : String) = viewModelScope.launch {
        try {
            val response = fcmRepository.getToken(name)
            if (response.isSuccess) {
                _retrieveToken.value = response.result.tokens
                // 서버에서 받은 토큰을 사용해 로직을 처리
                Log.d("Token Retrieval", retrieveToken.value.toString())
            } else {
                Log.e("Token Retrieval", "토큰 조회 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("Token Retrieval", "토큰 조회 중 오류 발생: ${e.message}")

        }
    }

    private fun deleteFcmToken(token : String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenRequest = Token(token)
                fcmRepository.deleteToken(tokenRequest)
                Log.d("deleteToken", "성공")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("deleteToken", "토큰 삭제 실패: ${e.message}")
            }
        }
    }

    fun fcmTokenDelete(name : String){
        retrieveTokenFromServer(name)
        _retrieveToken.value?.let { tokens ->
            for (token in tokens) {
                deleteFcmToken(token)
            }

        }
        resetToken()
    }

    private fun resetToken(){
        _retrieveToken.value = emptyList()
    }

}