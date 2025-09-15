package com.toyou.toyouandroid.presentation.fragment.home

import com.toyou.toyouandroid.data.emotion.dto.DiaryCard
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCard

data class HomeUiState(
    val currentDate: String = "",
    val emotionText: String = "멘트",
    val diaryCards: List<DiaryCard>? = null,
    val yesterdayCards: List<YesterdayCard> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)