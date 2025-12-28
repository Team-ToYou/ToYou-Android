package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupAgreeViewModel @Inject constructor() : ViewModel() {

    private val _imageStates = MutableLiveData(listOf(false, false, false, false))
    val imageStates: LiveData<List<Boolean>> get() = _imageStates

    private val _isNextButtonEnabled = MutableLiveData(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    private val _nextButtonTextColor = MutableLiveData<Int>().apply {
        value = 0xFFA6A6A6.toInt()
    }
    val nextButtonTextColor: LiveData<Int> = _nextButtonTextColor

    private val _nextButtonBackground = MutableLiveData<Int>().apply {
        value = R.drawable.next_button
    }
    val nextButtonBackground: LiveData<Int> = _nextButtonBackground

    private val targetImageResId = R.drawable.checkbox_checked // 목표 이미지의 리소스 ID

    fun onImageClicked(index: Int, newImageResId: Int) {
        val newImageStates = _imageStates.value?.toMutableList() ?: mutableListOf()

        if (index == 0) {
            // 첫 번째 이미지뷰 클릭 시, 모든 이미지 상태를 업데이트
            val newState = newImageResId == targetImageResId
            for (i in newImageStates.indices) {
                newImageStates[i] = newState
            }
        } else {
            // 다른 이미지뷰 클릭 시, 해당 이미지 상태만 업데이트
            newImageStates[index] = newImageResId == targetImageResId
        }

        _imageStates.value = newImageStates

        val isSecondImageChecked = newImageStates[1]
        val isThirdImageChecked = newImageStates[2]
        val isFourthImageChecked = newImageStates[3]
        val allRequiredImagesChecked = isSecondImageChecked && isThirdImageChecked && isFourthImageChecked
        _isNextButtonEnabled.value = allRequiredImagesChecked

        // 다음 버튼의 텍스트 색깔과 배경을 업데이트
        if (allRequiredImagesChecked) {
            _nextButtonTextColor.value = 0xFF000000.toInt() // 활성화 상태의 텍스트 색상
            _nextButtonBackground.value = R.drawable.next_button_enabled // 활성화 상태의 배경
        } else {
            _nextButtonTextColor.value = 0xFFA6A6A6.toInt() // 비활성화 상태의 텍스트 색상
            _nextButtonBackground.value = R.drawable.next_button // 비활성화 상태의 배경
        }
    }
}