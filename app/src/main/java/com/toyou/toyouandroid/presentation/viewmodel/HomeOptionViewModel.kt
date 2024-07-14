package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigator
import com.toyou.toyouandroid.di.FragmentNavigator

class HomeOptionViewModel (
    private val navigator: FragmentNavigator,
    private val homeResultViewModel: HomeResultViewModel
) : ViewModel() {

    fun onStampSelected(stamp: String) {
        homeResultViewModel.setSelectedStamp(stamp)
        navigator.navigateToResultFragment()
    }
}
