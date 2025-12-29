package com.toyou.feature.record.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.DiaryCardNum
import com.toyou.core.domain.model.DiaryCardPerDay
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.repository.IRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendRecordViewModel @Inject constructor(
    private val repository: IRecordRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<FriendRecordUiState, FriendRecordEvent, FriendRecordAction>(
    initialState = FriendRecordUiState()
) {

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
                when (val result = repository.getFriendRecordNum(year, month)) {
                    is DomainResult.Success -> {
                        val cardList = result.data.cards
                        updateState { copy(diaryCardsNum = cardList) }
                        Timber.tag("FriendRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                        Timber.tag("FriendRecordViewModel").d("API Success: Received $cardList")
                    }
                    is DomainResult.Error -> {
                        val errorMessage = result.message
                        performHandleError(errorMessage)
                        Timber.tag("FriendRecordViewModel").d("API Error: $errorMessage")

                        errorHandler.handleError(
                            error = result,
                            onRetry = { performLoadDiaryCardsNum(year, month) },
                            onFailure = { Timber.e("loadDiaryCards API call failed") },
                            tag = "FriendRecordViewModel"
                        )
                    }
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
                when (val result = repository.getFriendRecordPerDay(year, month, day)) {
                    is DomainResult.Success -> {
                        val cardList = result.data.cards
                        updateState { copy(diaryCardPerDay = cardList) }
                        Timber.tag("FriendRecordViewModel").d("API Success: Received ${cardList.size} diary cards")
                        Timber.tag("FriendRecordViewModel").d("API Success: Received $cardList")
                    }
                    is DomainResult.Error -> {
                        val errorMessage = result.message
                        performHandleError(errorMessage)
                        Timber.tag("FriendRecordViewModel").d("API Error: $errorMessage")

                        errorHandler.handleError(
                            error = result,
                            onRetry = { performLoadDiaryCardPerDay(year, month, day) },
                            onFailure = { Timber.e("loadDiaryCards API call failed") },
                            tag = "FriendRecordViewModel"
                        )
                    }
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
