package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.model.FriendListModel
import com.toyou.toyouandroid.model.QuestionTypeModel

class SocialViewModel : ViewModel() {
    private val _friends = MutableLiveData<List<FriendListModel>>()
    val friends : LiveData<List<FriendListModel>> get () = _friends
    private val _clickedPosition = MutableLiveData<Map<Int, Boolean>>()
    val clickedPosition : LiveData<Map<Int, Boolean>> get() = _clickedPosition

    private val _selectedChar = MutableLiveData<Int>()
    val selectedChar : LiveData<Int> get() = _selectedChar
    private val _nextBtnEnabled = MutableLiveData<Boolean>()
    val nextBtnEnabled : LiveData<Boolean> get() = _nextBtnEnabled

    private val _plusBoxVisibility = MutableLiveData<List<Boolean>>()
    val plusBoxVisibility : MutableLiveData<List<Boolean>> get() = _plusBoxVisibility

    init {
        loadFriendData()
        loadInitQuestionType()
        _selectedChar.value = -1
        _nextBtnEnabled.value = false
    }

    fun loadFriendData(){
        val sampleData = listOf(
            FriendListModel("jjeong", "평범한 하루였어요"),
            FriendListModel("jjeong", "평범한 하루였어요"),
            FriendListModel("jjeong", "평범한 하루였어요"),
        )
        _friends.value = sampleData
    }

    fun loadInitQuestionType(){
        val initialMap = mapOf(
            1 to false,
            2 to false,
            3 to false
        )
        _clickedPosition.value = initialMap
    }

    fun updateClickState(key: Int) {
        val currentMap = _clickedPosition.value?.toMutableMap() ?: mutableMapOf()
        currentMap[key] = !(currentMap[key] ?: false)


        _clickedPosition.value = currentMap
    }

    fun onCharSelected(position : Int){
        _selectedChar.value = if (_selectedChar.value == position) -1 else position
        _nextBtnEnabled.value = _selectedChar.value != -1
    }

    fun togglePlusBoxVisibility() {
        val visibility = _plusBoxVisibility.value ?: listOf(false, false, false)
        val newVisibility = visibility.toMutableList()

        //조건에 맞는게 나타나면 그 뒤로 실행x
        when {
            !newVisibility[0] -> newVisibility[0] = true
            !newVisibility[1] -> newVisibility[1] = true
            !newVisibility[2] -> newVisibility[2] = true
        }
        _plusBoxVisibility.value = newVisibility
    }

    fun hidePlusBox(index: Int) {
        val visibility = _plusBoxVisibility.value ?: listOf(false, false, false)
        val newVisibility = visibility.toMutableList()
        newVisibility[index] = false
        _plusBoxVisibility.value = newVisibility
    }

}