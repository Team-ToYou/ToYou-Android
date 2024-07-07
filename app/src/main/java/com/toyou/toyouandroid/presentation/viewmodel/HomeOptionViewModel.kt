package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigator
import com.toyou.toyouandroid.di.FragmentNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeOptionViewModel @Inject constructor(
    private val navigator: FragmentNavigator
) : ViewModel() {

    private val _selectedStamp = MutableLiveData<String>()
    val selectedStamp: LiveData<String> get() = _selectedStamp

    fun onStampSelected(stamp: String) {
        _selectedStamp.value = stamp
        navigator.navigateToResultFragment()
    }
}
