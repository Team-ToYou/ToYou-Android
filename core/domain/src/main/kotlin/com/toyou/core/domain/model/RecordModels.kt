package com.toyou.core.domain.model

data class DiaryCard(
    val cardId: Int,
    val emotion: String?,
    val date: String
)

data class DiaryCardList(
    val cards: List<DiaryCard>
)

data class DiaryCardNum(
    val cardNum: Int,
    val date: String
)

data class DiaryCardNumList(
    val cards: List<DiaryCardNum>
)

data class DiaryCardPerDay(
    val cardId: Int,
    val nickname: String,
    val emotion: String
)

data class DiaryCardPerDayList(
    val cards: List<DiaryCardPerDay>
)

data class CardExposure(
    val exposure: Boolean
)
