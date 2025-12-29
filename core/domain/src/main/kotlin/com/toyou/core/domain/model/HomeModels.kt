package com.toyou.core.domain.model

data class HomeInfo(
    val nickname: String,
    val emotion: String?,
    val questionCount: Int,
    val cardId: Int?,
    val hasUncheckedAlarm: Boolean
)

data class CardDetailInfo(
    val date: String,
    val receiver: String,
    val emotion: String,
    val exposure: Boolean,
    val questions: List<QuestionAnswer>
)

data class QuestionAnswer(
    val id: Long,
    val content: String,
    val type: String,
    val questioner: String,
    val answer: String,
    val options: List<String>?
)

data class YesterdayCardInfo(
    val cardId: Long,
    val date: String,
    val receiver: String,
    val emotion: String,
    val exposure: Boolean,
    val questions: List<QuestionAnswer>
)

data class YesterdayCardList(
    val cards: List<YesterdayCardInfo>
)

data class CreateQuestionInfo(
    val id: Long,
    val content: String,
    val type: String,
    val questioner: String,
    val options: List<String>
)

data class CreateQuestionList(
    val questions: List<CreateQuestionInfo>
)

data class CardPostResult(
    val cardId: Int
)
