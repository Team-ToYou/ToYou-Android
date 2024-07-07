package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.di.FragmentNavigator
import com.toyou.toyouandroid.model.HomeOptionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeResultViewModel @Inject constructor(
    private val navigator: FragmentNavigator
) : ViewModel() {

    private val _sentence = MutableLiveData<HomeOptionResult>()
    val sentence: LiveData<HomeOptionResult> get() = _sentence

    init {
        viewModelScope.launch {
            delay(2000)
            navigator.navigateToHomeFragment()
        }
    }
}
