package com.toyou.toyouandroid.presentation.fragment.emotionstamp.network

import android.view.View

data class EmotionResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)

data class EmotionRequest(
    var emotion: String
)

data class EmotionData(
    val imageView: View,
    val emotion: EmotionRequest,
    val homeEmotionDrawable: Int,
    val homeEmotionTitle: String,
    val homeColorRes: Int,
    val backgroundDrawable: Int,
    val mypageEmotionDrawable: Int
)

data class YesterdayFriendsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ResultData
)

data class ResultData(
    val yesterday: List<DiaryCard>
)

data class DiaryCard(
    val cardId: Int,
    val nickname: String
)