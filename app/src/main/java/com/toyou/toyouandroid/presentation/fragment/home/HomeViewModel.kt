package com.toyou.toyouandroid.presentation.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.data.emotion.dto.DiaryCard
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.data.emotion.dto.YesterdayFriendsResponse
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.calendar.getCurrentDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class HomeViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _homeEmotion = MutableLiveData<Int>()
    val homeEmotion: LiveData<Int> get() = _homeEmotion

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    private val _homeDateBackground = MutableLiveData<Int>()
    val homeDateBackground: LiveData<Int> get() = _homeDateBackground

    private val _homeBackground = MutableLiveData<Int>()
    val homeBackground: LiveData<Int> get() = _homeBackground

    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> get() = _currentDate

    private val _diaryCards = MutableLiveData<List<DiaryCard>?>()
    val diaryCards: LiveData<List<DiaryCard>?> get() = _diaryCards

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    private val apiService: EmotionService = AuthNetworkModule.getClient().create(EmotionService::class.java)

    init {
        _currentDate.value = getCurrentDate()
    }

    fun updateHomeEmotion(emotion: Int, text: String, date: Int, background: Int) {
        _homeEmotion.value = emotion
        _text.value = text
        _homeDateBackground.value = date
        _homeBackground.value = background
    }

    fun resetState() {
        _homeEmotion.value = R.drawable.home_emotion_none
        _text.value = "멘트"
        _homeDateBackground.value = R.color.g00
        _homeBackground.value = R.drawable.background_white
    }

    fun loadYesterdayDiaryCards() {
        _isLoading.value = true

        tokenManager.refreshToken(
            onSuccess = { token ->
                Timber.d("API 호출 - 사용된 Access Token: $token")

                apiService.getYesterdayFriendCard().enqueue(object : Callback<YesterdayFriendsResponse> {
                    override fun onResponse(call: Call<YesterdayFriendsResponse>, response: Response<YesterdayFriendsResponse>) {
                        if (response.isSuccessful) {
                            val diaryResponse = response.body()
                            Timber.d("API 응답 데이터: $diaryResponse")

                            if (diaryResponse?.isSuccess == true) {
                                val cards = diaryResponse.result.yesterday
                                Timber.d("받아온 카드 목록: $cards")

                                if (cards.isEmpty()) {
                                    Timber.d("카드 목록이 비어 있습니다.")
                                    _isEmpty.value = true
                                } else {
                                    _diaryCards.value = cards
                                    _isEmpty.value = false
                                }
                            } else {
                                Timber.e("API 호출 실패 - 응답이 성공하지 않음. 메시지: ${diaryResponse?.message}")
                            }
                        } else {
                            Timber.e("API 호출 실패 - 코드: ${response.code()}, 메시지: ${response.message()}")
                        }

                        _isLoading.value = false
                    }

                    override fun onFailure(call: Call<YesterdayFriendsResponse>, t: Throwable) {
                        Timber.e(t, "API 호출 실패 - 네트워크 오류")
                        _isLoading.value = false
                        _isEmpty.value = true // 실패 시 빈 상태로 간주
                    }
                })
            },
            onFailure = {
                Timber.e("토큰 갱신 실패 - API 호출 중단")
                _isLoading.value = false
            }
        )
    }


}
