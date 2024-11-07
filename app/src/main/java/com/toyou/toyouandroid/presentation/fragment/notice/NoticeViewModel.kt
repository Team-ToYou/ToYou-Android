package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.domain.notice.NoticeRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class NoticeViewModel(
    private val repository: NoticeRepository,
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _noticeItems = MutableLiveData<List<NoticeItem>?>()
    val noticeItems: MutableLiveData<List<NoticeItem>?> get() = _noticeItems

    private val _hasNotifications = MutableLiveData<Boolean>()
    val hasNotifications: LiveData<Boolean> get() = _hasNotifications

    fun fetchNotices() {
        viewModelScope.launch {
            val response = repository.getNotices()
            response.enqueue(object : Callback<AlarmResponse> {
                override fun onResponse(
                    call: Call<AlarmResponse>,
                    response: Response<AlarmResponse>
                ) {
                    if (response.isSuccessful) {
                        val alarmResponse = response.body()
                        val items = alarmResponse?.result?.alarmList?.mapNotNull { alarm ->
                            when (alarm.alarmType) {
                                "FRIEND_REQUEST" -> NoticeItem.NoticeFriendRequestItem(
                                    alarm.nickname,
                                    alarm.alarmId
                                )
                                "REQUEST_ACCEPTED" -> NoticeItem.NoticeFriendRequestAcceptedItem(
                                    alarm.nickname,
                                    alarm.alarmId
                                )
                                "NEW_QUESTION" -> NoticeItem.NoticeCardCheckItem(
                                    alarm.nickname,
                                    alarm.alarmId
                                )
                                else -> null
                            }
                        } ?: emptyList()

                        _hasNotifications.value = items.isNotEmpty()
                        _noticeItems.value = items
                    } else {
                        refreshAccessToken(::fetchNotices)
                    }
                }

                override fun onFailure(call: Call<AlarmResponse>, t: Throwable) {
                    // 에러 처리 로직
                }
            })
        }
    }

    fun deleteNotice(alarmId: Int, position: Int) {
        viewModelScope.launch {
            val response = repository.deleteNotice(alarmId)
            response.enqueue(object : Callback<AlarmDeleteResponse> {
                override fun onResponse(
                    call: Call<AlarmDeleteResponse>,
                    response: Response<AlarmDeleteResponse>
                ) {
                    if (response.isSuccessful) {
                        val updatedList = _noticeItems.value?.toMutableList()?.apply {
                            removeAt(position)
                        }
                        _noticeItems.value = updatedList
                    } else {
                        refreshAccessToken { deleteNotice(alarmId, position) }
                    }
                }

                override fun onFailure(call: Call<AlarmDeleteResponse>, t: Throwable) {
                    // 에러 처리 로직
                }
            })
        }
    }

    private fun refreshAccessToken(onSuccess: () -> Unit) {
        authService.reissue(tokenStorage.getRefreshToken().toString()).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                if (response.isSuccessful) {
                    response.headers()["access_token"]?.let { newAccessToken ->
                        response.headers()["refresh_token"]?.let { newRefreshToken ->
                            // 새로운 토큰 저장
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                            AuthNetworkModule.setAccessToken(newAccessToken)

                            // 성공 콜백 실행
                            onSuccess()
                        } ?: Timber.e("Refresh token missing in response headers")
                    } ?: Timber.e("Access token missing in response headers")
                } else {
                    Timber.e("API Error: ${response.errorBody()?.string() ?: "Unknown error"}")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Timber.e(t, "Error occurred during token refresh")
            }
        })
    }
}
