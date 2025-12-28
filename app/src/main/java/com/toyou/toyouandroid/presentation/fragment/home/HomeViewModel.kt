package com.toyou.toyouandroid.presentation.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.domain.home.repository.HomeRepository
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.calendar.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableLiveData(HomeUiState())
    val uiState: LiveData<HomeUiState> get() = _uiState

    init {
        _uiState.value = _uiState.value?.copy(
            currentDate = getCurrentDate()
        )
    }

    fun updateHomeEmotion(emotionText: String) {
        _uiState.value = _uiState.value?.copy(
            emotionText = emotionText
        )
    }

    fun resetState() {
        _uiState.value = _uiState.value?.copy(
            emotionText = "멘트",
            diaryCards = null,
            yesterdayCards = emptyList()
        )
    }

    fun getYesterdayCard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)
            try {
                val response = homeRepository.getYesterdayCard()
                Timber.tag("HomeViewModel").d("yesterdayCards: ${response.result}")
                if (response.isSuccess) {
                    _uiState.value = _uiState.value?.copy(
                        yesterdayCards = response.result.yesterday,
                        isLoading = false,
                        isEmpty = response.result.yesterday.isEmpty()
                    )
                    Timber.tag("HomeViewModel").d("yesterdayCards: ${response.result.yesterday}")
                } else {
                    _uiState.value = _uiState.value?.copy(isLoading = false)
                    tokenManager.refreshToken(
                        onSuccess = { getYesterdayCard() },
                        onFailure = { 
                            Timber.tag("HomeViewModel").d("refresh error")
                            _uiState.value = _uiState.value?.copy(
                                isLoading = false,
                                yesterdayCards = emptyList()
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("HomeViewModel").e(e, "Error getting yesterday cards")
                _uiState.value = _uiState.value?.copy(
                    yesterdayCards = emptyList(),
                    isLoading = false,
                    isEmpty = true
                )
            }
        }
    }
}