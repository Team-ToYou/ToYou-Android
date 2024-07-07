package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.model.CardModel

class CardViewModel : ViewModel(){
    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards


    //뷰모델이 생성될때 초기값 설정
    init {
        loadCardData()
    }


    fun loadCardData(){
        val sampleCards = listOf(
            CardModel("요즘 어떻게 지내?", "From. 현정"),
            CardModel("요즘 즐겨하는 취미는?", "From. 현정"),
            CardModel("짜장면 vs 짬뽕", "From. 현정")
        )
        Log.d("CardViewModel", "Loading cards: $sampleCards") // 디버그 로그 추가

        _cards.value = sampleCards
    }

    fun updateButtonState(position : Int, isSelected : Boolean){
        _cards.value = _cards.value?.mapIndexed { index, card ->
            if (index == position) {
                card.copy(isButtonSelected = isSelected)
            } else {
                card
            }
        }
    }
}