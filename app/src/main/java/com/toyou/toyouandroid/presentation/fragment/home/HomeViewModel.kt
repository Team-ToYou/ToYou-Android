package com.toyou.toyouandroid.presentation.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.data.emotion.dto.DiaryCard
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.data.emotion.dto.YesterdayFriendsResponse
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCard
import com.toyou.toyouandroid.domain.home.repository.HomeRepository
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.calendar.getCurrentDate
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.math.log

class HomeViewModel(private val tokenManager: TokenManager,
    private val repository: HomeRepository) : ViewModel() {

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

    private val _yesterdayCards = MutableLiveData<List<YesterdayCard>>()
    val yesterdayCards: LiveData<List<YesterdayCard>> = _yesterdayCards

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
    fun getYesterdayCard() {
        viewModelScope.launch {
            try {
                val response = repository.getYesterdayCard()
                if (response.isSuccess) {
                    _yesterdayCards.value = response.result.yesterday
                    Timber.tag("어제 일기")
                } else {
                    tokenManager.refreshToken(
                        onSuccess = { getYesterdayCard() },
                        onFailure = { Timber.tag("HomeViewModel").d("refresh error") }
                    )
                }
            } catch (e: Exception) {
                _yesterdayCards.value = emptyList()
            }
        }
    }

}
