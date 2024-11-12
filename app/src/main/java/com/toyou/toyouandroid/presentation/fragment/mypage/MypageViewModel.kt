package com.toyou.toyouandroid.presentation.fragment.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.data.mypage.dto.MypageResponse
import com.toyou.toyouandroid.data.mypage.service.MypageService
import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MypageViewModel(private val authService: AuthService, private val tokenStorage: TokenStorage) : ViewModel() {

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    fun setLogoutSuccess(value: Boolean) {
        _logoutSuccess.value = value
    }

    fun kakaoLogout() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            val accessToken = tokenStorage.getAccessToken().toString()
            Timber.d("Attempting to logout in with refresh token: $refreshToken")
            Timber.d("accessToken: $accessToken")

            authService.logout(refreshToken).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        Timber.i("Logout successfully")
                        _logoutSuccess.value = true
                        tokenStorage.clearTokens()
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Timber.e("API Error: $errorMessage")
                        _logoutSuccess.value = false
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    val errorMessage = t.message ?: "Unknown error"
                    Timber.e("Network Failure: $errorMessage")
                    _logoutSuccess.value = false
                }
            })
        }
    }

    private val _signOutSuccess = MutableLiveData<Boolean>()
    val signOutSuccess: LiveData<Boolean> get() = _signOutSuccess

    fun setSignOutSuccess(value: Boolean) {
        _signOutSuccess.value = value
    }

    fun kakaoSignOut() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken().toString()
            val accessToken = tokenStorage.getAccessToken().toString()
            Timber.d("Attempting to signout in with refresh token: $refreshToken")
            Timber.d("accessToken: $accessToken")

            authService.signOut(refreshToken).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        Timber.i("SignOut successfully")
                        _signOutSuccess.value = true
                        tokenStorage.clearTokens()
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Timber.e("API Error: $errorMessage")
                        _signOutSuccess.value = false
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    val errorMessage = t.message ?: "Unknown error"
                    Timber.e("Network Failure: $errorMessage")
                    _signOutSuccess.value = false
                }
            })
        }
    }

//    private val myPageService: MypageService = TestNetworkModule.getClient(
//        TokenManager(authService, tokenStorage),
//        tokenStorage
//    ).create(MypageService::class.java)

    private val myPageService: MypageService = AuthNetworkModule.getClient().create(MypageService::class.java)

    private val _friendNum = MutableLiveData<Int?>()
    val friendNum: LiveData<Int?> get() = _friendNum

    private val _nickname = MutableLiveData<String?>()
    val nickname: LiveData<String?> get() = _nickname

    private val _status = MutableLiveData<String?>()
    val status: LiveData<String?> get() = _status

    fun updateMypage() {
        val call = myPageService.getMypage()

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
                    Timber.tag("updateStatus").d("Status updated: $status")
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
}