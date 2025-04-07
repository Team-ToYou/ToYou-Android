package com.toyou.toyouandroid.data.emotion.dto

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
    val cardContent: CardContent
)

data class CardContent(
    val date: String,
    val receiver: String,
    val emotion: String,
    val exposure: Boolean,
    val questionList: List<Question>
)

data class Question(
    val questionId: Int,
    val content: String,
    val questionType: String,
    val questioner: String,
    val answer: String,
    val answerOption: List<String>
)