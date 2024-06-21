package com.toyou.toyouandroid.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.model.CardModel

class CardViewModel : ViewModel(){
    private val _cards = MutableLiveData<List<CardModel>>()
    val cards: LiveData<List<CardModel>> get() = _cards

    private fun loadCardData(){
        val sampleUsers = listOf(
            CardModel("요즘 어떻게 지내?"),
            CardModel("요즘 즐겨하는 취미는?"),
            CardModel("짜장면 vs 짬뽕")
        )
        _cards.value = sampleUsers
    }
}