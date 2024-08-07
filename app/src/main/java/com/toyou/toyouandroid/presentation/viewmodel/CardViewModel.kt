package com.toyou.toyouandroid.presentation.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.model.CardModel
import com.toyou.toyouandroid.model.PreviewCardModel

class CardViewModel : ViewModel(){
    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards

    private val _previewCards = MutableLiveData<List<PreviewCardModel>>()
    val previewCards : LiveData<List<PreviewCardModel>> get() = _previewCards


    fun isLockSelected(lock : ImageView){
        lock.isSelected = !lock.isSelected
    }


    //뷰모델이 생성될때 초기값 설정
    init {
        loadCardData()
    }

    fun loadCardData(){
        val sampleCards = listOf(
            CardModel("요즘 어떻게 지내?", "From. 현정",1),
            //CardModel("요즘 즐겨하는 취미는?", "From. 현정"),
            CardModel("짜장면 vs 짬뽕", "From. 현정",2),
            CardModel("오늘 몇시에 잘거야?", "From. 현정",3),
            CardModel("오늘 커피 몇잔 마셨어?", "From. 현정",4),
            CardModel("오늘 물 몇잔 마셨어?", "From. 현정",5),

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

    fun updatePreviewCard(){
        _previewCards.value = _cards.value?.filter {it.isButtonSelected}?.map {
            PreviewCardModel(answer = "", question = it.message, type = it.questionType)
        }
    }


}