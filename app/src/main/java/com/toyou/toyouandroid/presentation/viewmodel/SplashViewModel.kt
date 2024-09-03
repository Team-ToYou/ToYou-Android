package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val repository = CreateRepository()
    private val _cardId = MutableLiveData<Int>().apply { value = 0 }
    val cardId: LiveData<Int> get() = _cardId
    private val _emotion = MutableLiveData<String>().apply { value = null }
    val emotion : LiveData<String> get() = _emotion

    fun getHomeEntry(onSuccess: () -> Unit) = viewModelScope.launch {
        try {
            val response = repository.getHomeEntryData()
            if (response.isSuccess){
                _cardId.value = response.result.id
                _emotion.value = response.result.emotion
                Log.d("get home","api 성공")
                onSuccess()  // API 성공 시 콜백 호출
            } else {
                Log.e("get home", "home API 호출 실패: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("get home", "home API 예외 발생: ${e.message}")
        }
    }
}
