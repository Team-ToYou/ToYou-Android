package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.utils.TokenManager
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel(
    private val tokenManager: TokenManager,
    private val repository: CreateRepository
) : ViewModel() {

    private val _cardId = MutableLiveData<Int?>()
    val cardId: LiveData<Int?> get() = _cardId

    private val _emotion = MutableLiveData<String?>()
    val emotion : LiveData<String?> get() = _emotion

    private val _nickname = MutableLiveData<String>()
    val nickname : LiveData<String> get() = _nickname

    private val _cardNum = MutableLiveData<Int>()
    val cardNum : LiveData<Int> get() = _cardNum

    private val _uncheckedAlarm = MutableLiveData<Boolean>()
    val uncheckedAlarm : LiveData<Boolean> get() = _uncheckedAlarm

    fun getHomeEntry() {
        viewModelScope.launch {
            try {
                val response = repository.getHomeEntryData()
                if (response.isSuccess) {
                    _cardId.value = response.result.id
                    _emotion.value = response.result.emotion
                    _nickname.value = response.result.nickname
                    _cardNum.value = response.result.question
                    _uncheckedAlarm.value = response.result.alarm

                    Timber.tag("UserViewModel").d("API 성공, ${response.result}")
                } else {
                    Timber.tag("UserViewModel").d("API 실패: ${response.message}")
                    tokenManager.refreshToken(
                        onSuccess = { getHomeEntry() },
                        onFailure = { Timber.e("getHomeEntry API call failed") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("UserViewModel").d("예외 발생: ${e.message}")
                tokenManager.refreshToken(
                    onSuccess = { getHomeEntry() },
                    onFailure = { Timber.e("getHomeEntry API call failed") }
                )
            }
        }
    }

    fun updateCardIdFromOtherViewModel(otherViewModel: CardViewModel) {
        otherViewModel.cardId.observeForever { newCardId ->
            _cardId.value = newCardId
        }
    }
}
