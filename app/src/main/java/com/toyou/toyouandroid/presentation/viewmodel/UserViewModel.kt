package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel(private val tokenStorage: TokenStorage) : ViewModel() {

    private val repository = CreateRepository(tokenStorage)

    private val _cardId = MutableLiveData<Int?>()
    val cardId: LiveData<Int?> get() = _cardId

    private val _emotion = MutableLiveData<String?>()
    val emotion : LiveData<String?> get() = _emotion

    private val _nickname = MutableLiveData<String>()
    val nickname : LiveData<String> get() = _nickname

    private val _cardNum = MutableLiveData<Int>()
    val cardNum : LiveData<Int> get() = _cardNum

    fun getHomeEntry() = viewModelScope.launch {
        try {
            val response = repository.getHomeEntryData()
            if (response.isSuccess) {
                _cardId.value = response.result.id
                _emotion.value = response.result.emotion
                _nickname.value = response.result.nickname
                _cardNum.value = response.result.question

                Timber.tag("getHomeEntry").d("API 성공, 카드 ID: ${response.result.id}")
                Timber.tag("getHomeEntry").d("API 성공, 상태: ${response.result.emotion}")
                Timber.tag("getHomeEntry").d("API 성공, 카드 ID: ${response.result.nickname}")

            } else {
                Log.e("get home", "home API 호출 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("get home", "home API 예외 발생: ${e.message}")
        }
    }


    fun updateCardIdFromOtherViewModel(otherViewModel: CardViewModel) {
        otherViewModel.cardId.observeForever { newCardId ->
            _cardId.value = newCardId
        }
    }
}
