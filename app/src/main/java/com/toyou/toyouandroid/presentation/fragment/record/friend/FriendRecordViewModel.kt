package com.toyou.toyouandroid.presentation.fragment.record.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.data.record.dto.DiaryCardNum
import com.toyou.toyouandroid.data.record.dto.DiaryCardPerDay
import com.toyou.toyouandroid.domain.record.IRecordRepository
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendRecordViewModel @Inject constructor(
    private val repository: IRecordRepository,
    private val tokenManager: TokenManager
) : MviViewModel<FriendRecordUiState, FriendRecordEvent, FriendRecordAction>(
    initialState = FriendRecordUiState()
) {

    private val _diaryCardsNum = MutableLiveData<List<DiaryCardNum>>()
    val diaryCardsNum: LiveData<List<DiaryCardNum>> get() = _diaryCardsNum

    private val _diaryCardPerDay = MutableLiveData<List<DiaryCardPerDay>>()
    val diaryCardPerDay: LiveData<List<DiaryCardPerDay>> get() = _diaryCardPerDay

    init {
        state.onEach { uiState ->
            _diaryCardsNum.value = uiState.diaryCardsNum
            _diaryCardPerDay.value = uiState.diaryCardPerDay
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: FriendRecordAction) {
        when (action) {
            is FriendRecordAction.LoadDiaryCardsNum -> performLoadDiaryCardsNum(action.year, action.month)
            is FriendRecordAction.LoadDiaryCardPerDay -> performLoadDiaryCardPerDay(action.year, action.month, action.day)
        }
    }

    private fun performLoadDiaryCardsNum(year: Int, month: Int) {
        Timber.tag("FriendRecordViewModel").d("loadDiaryCards called with year: $year, month: $month")

        viewModelScope.launch {
            try {
                val response = repository.getFriendRecordNum(year, month)
                if (response.isSuccessful) {
                    val cardList = response.body()?.result?.cardList ?: emptyList()
                    updateState { copy(diaryCardsNum = cardList) }
                    Timber.tag("FriendRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                    Timber.tag("FriendRecordViewModel").d("API Success: Received $cardList")
                } else {
                    val errorMessage = response.message()
                    performHandleError(errorMessage)
                    Timber.tag("FriendRecordViewModel").d("API Error: $errorMessage")

                    tokenManager.refreshToken(
                        onSuccess = { performLoadDiaryCardsNum(year, month) },
                        onFailure = { Timber.e("loadDiaryCards API call failed") }
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                performHandleError(errorMessage)
                Timber.tag("FriendRecordViewModel").d("Network Failure: $errorMessage")
            }
        }
    }

    private fun performLoadDiaryCardPerDay(year: Int, month: Int, day: Int) {
        Timber.tag("FriendRecordViewModel").d("loadDiaryCards called with year: $year, month: $month")

        viewModelScope.launch {
            try {
                val response = repository.getFriendRecordPerDay(year, month, day)
                if (response.isSuccessful) {
                    val cardList = response.body()?.result?.cardList ?: emptyList()
                    updateState { copy(diaryCardPerDay = cardList) }
                    Timber.tag("FriendRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                    Timber.tag("FriendRecordViewModel").d("API Success: Received $cardList")
                } else {
                    val errorMessage = response.message()
                    performHandleError(errorMessage)
                    Timber.tag("FriendRecordViewModel").d("API Error: $errorMessage")

                    tokenManager.refreshToken(
                        onSuccess = { performLoadDiaryCardPerDay(year, month, day) },
                        onFailure = { Timber.e("loadDiaryCards API call failed") }
                    )
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                performHandleError(errorMessage)
                Timber.tag("FriendRecordViewModel").d("Network Failure: $errorMessage")
            }
        }
    }

    private fun performHandleError(message: String) {
        Timber.tag("FriendRecordViewModel").d("Error: $message")
        sendEvent(FriendRecordEvent.ShowError(message))
    }

    fun loadDiaryCardsNum(year: Int, month: Int) = onAction(FriendRecordAction.LoadDiaryCardsNum(year, month))
    fun loadDiaryCardPerDay(year: Int, month: Int, day: Int) = onAction(FriendRecordAction.LoadDiaryCardPerDay(year, month, day))
}
