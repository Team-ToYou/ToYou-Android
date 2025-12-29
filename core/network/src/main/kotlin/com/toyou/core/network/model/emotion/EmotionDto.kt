package com.toyou.core.network.model.emotion

data class EmotionResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)

data class EmotionRequest(
    var emotion: String
)

data class YesterdayFriendsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ResultData
)

data class ResultData(
    val yesterday: List<EmotionDiaryCard>
)

data class EmotionDiaryCard(
    val cardId: Int,
    val cardContent: EmotionCardContent
)

data class EmotionCardContent(
    val date: String,
    val receiver: String,
    val emotion: String,
    val exposure: Boolean,
    val questionList: List<EmotionQuestion>
)

data class EmotionQuestion(
    val questionId: Int,
    val content: String,
    val questionType: String,
    val questioner: String,
    val answer: String,
    val answerOption: List<String>
)
