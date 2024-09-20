package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.presentation.fragment.notice.network.NetworkModule
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.NicknameCheckResponse
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.OnboardingService
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.PatchNicknameResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SignupNicknameViewModel : ViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _backButtonAction = MutableLiveData<() -> Unit>()
    val backButtonAction: LiveData<() -> Unit> get() = _backButtonAction

    private val _textCount = MutableLiveData("0/15")
    val textCount: LiveData<String> get() = _textCount

    fun updateTextCount(count: Int) {
        _textCount.value = "$count/15"
    }

    private val inputText = MutableLiveData<String>()

    private val _isDuplicateCheckEnabled = MutableLiveData(false)
    val isDuplicateCheckEnabled: LiveData<Boolean> = _isDuplicateCheckEnabled

    private val _duplicateCheckMessage = MutableLiveData<String>().apply {
        value = "중복된 닉네임인지 확인해주세요"
    }
    val duplicateCheckMessage: LiveData<String> = _duplicateCheckMessage

    private val _duplicateCheckMessageColor = MutableLiveData<Int>().apply {
        value = 0xFF000000.toInt()
    }
    val duplicateCheckMessageColor: LiveData<Int> = _duplicateCheckMessageColor

    private val _duplicateCheckButtonTextColor = MutableLiveData<Int>().apply {
        value = 0xFFA6A6A6.toInt()
    }
    val duplicateCheckButtonTextColor: LiveData<Int> = _duplicateCheckButtonTextColor

    private val _duplicateCheckButtonBackground = MutableLiveData<Int>().apply {
        value = R.drawable.next_button
    }
    val duplicateCheckButtonBackground: LiveData<Int> = _duplicateCheckButtonBackground


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

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    init {
        inputText.observeForever { text ->
            _isDuplicateCheckEnabled.value = !text.isNullOrEmpty()
        }
        _title.value = "회원가입"
//        _backButtonAction.value = { /* 회원가입 화면에서의 back 버튼 로직 */ }
    }

    fun duplicateBtnActivate() {
        _duplicateCheckButtonTextColor.value = 0xFF000000.toInt()
        _duplicateCheckButtonBackground.value = R.drawable.signupnickname_doublecheck_activate
    }

    fun updateLength15(length: Int) {
        if (length >= 15) {
            _duplicateCheckMessage.value = "15자 이내로 입력해주세요."
            _duplicateCheckMessageColor.value = 0xFF000000.toInt()
        } else {
            _duplicateCheckMessage.value = "중복된 닉네임인지 확인해주세요"
            _duplicateCheckMessageColor.value = 0xFF000000.toInt()
        }
    }

    fun setNickname(newNickname: String) {
        _nickname.value = newNickname
    }

    fun resetState() {
        _duplicateCheckMessage.value = "중복된 닉네임인지 확인해주세요"
        _duplicateCheckMessageColor.value = 0xFF000000.toInt()
        _isNextButtonEnabled.value = false
        _nextButtonTextColor.value = 0xFFA6A6A6.toInt()
        _nextButtonBackground.value = R.drawable.next_button
        _nickname.value = ""
        _duplicateCheckButtonTextColor.value = 0xFFA6A6A6.toInt()
        _duplicateCheckButtonBackground.value = R.drawable.next_button
    }

    private val retrofit = NetworkModule.getClient()
    private val apiService: OnboardingService = retrofit.create(OnboardingService::class.java)

    // API를 호출하여 닉네임 중복 체크를 수행하는 함수
    fun checkDuplicate() {
        val nickname = _nickname.value ?: return

        val call = apiService.getNicknameCheck(nickname)
        call.enqueue(object : Callback<NicknameCheckResponse> {
            override fun onResponse(call: Call<NicknameCheckResponse>, response: Response<NicknameCheckResponse>) {
                if (response.isSuccessful) {
                    val exists = response.body()?.result?.exists ?: false
                    if (!exists) {
                        _duplicateCheckMessage.value = "사용 가능한 닉네임입니다."
                        _duplicateCheckMessageColor.value = 0xFFEA9797.toInt()

                        _isNextButtonEnabled.value = true
                        _nextButtonTextColor.value = 0xFF000000.toInt()
                        _nextButtonBackground.value = R.drawable.next_button_enabled
                    } else {
                        _duplicateCheckMessage.value = "이미 사용 중인 닉네임입니다."
                        _duplicateCheckMessageColor.value = 0xFFFF0000.toInt()
                    }
                } else {
                    _duplicateCheckMessage.value = "닉네임 확인에 실패했습니다."
                    _duplicateCheckMessageColor.value = 0xFFFF0000.toInt()
                }
            }

            override fun onFailure(call: Call<NicknameCheckResponse>, t: Throwable) {
                Timber.tag("API Failure").e(t, "Error checking nickname")
                _duplicateCheckMessage.value = "서버에 연결할 수 없습니다."
                _duplicateCheckMessageColor.value = 0xFFFF0000.toInt()
            }
        })
    }
}