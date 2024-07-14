package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R

class HomeViewModel : ViewModel() {

    private val _selectedStamp = MutableLiveData<Int>()
    val selectedStamp: LiveData<Int> get() = _selectedStamp

//    init {
//        homeResultViewModel.selectedStamp.observeForever { stamp ->
//            _selectedStamp.value = when (stamp) {
//                "a" -> R.drawable.home_emotion_happy
//                "b" -> R.drawable.home_emotion_exciting
//                "c" -> R.drawable.home_emotion_normal
//                "d" -> R.drawable.home_emotion_anxiety
//                "e" -> R.drawable.home_emotion_upset
//                else -> R.drawable.home_emotion_none
//            }
//        }
//    }
}
