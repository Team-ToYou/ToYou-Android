package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.notice.dto.AlarmDeleteResponse
import com.toyou.toyouandroid.data.notice.dto.AlarmResponse
import com.toyou.toyouandroid.domain.notice.NoticeRepository
import com.toyou.toyouandroid.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class NoticeViewModel(
    private val repository: NoticeRepository,
    private val tokenManager: TokenManager
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
                        }?.reversed() ?: emptyList()

                        _hasNotifications.value = items.isNotEmpty()
                        _noticeItems.value = items
                    } else {
                        tokenManager.refreshToken(
                            onSuccess = { fetchNotices() }, // 토큰 갱신 후 다시 요청
                            onFailure = { Timber.e("Failed to refresh token and get notices") }
                        )
                    }
                }

                override fun onFailure(call: Call<AlarmResponse>, t: Throwable) {
                    // 에러 처리 로직
                }
            })
        }
    }

    fun deleteNotice(alarmId: Int, position: Int, retryCount: Int = 0) {
        val maxRetries = 5 // 최대 재시도 횟수

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
                        if (retryCount < maxRetries) {
                            tokenManager.refreshToken(
                                onSuccess = {
                                    Timber.d("Retrying token refresh... ($retryCount/$maxRetries)")
                                    deleteNotice(alarmId, position, retryCount + 1) // 재호출
                                },
                                onFailure = {
                                    Timber.e("Failed to refresh token after $retryCount retries")
                                }
                            )
                        } else {
                            Timber.e("Max token refresh retries reached ($maxRetries)")
                        }
                    }
                }

                override fun onFailure(call: Call<AlarmDeleteResponse>, t: Throwable) {
                    Timber.e("Delete notice API call failed: ${t.message}")
                }
            })
        }
    }
}
