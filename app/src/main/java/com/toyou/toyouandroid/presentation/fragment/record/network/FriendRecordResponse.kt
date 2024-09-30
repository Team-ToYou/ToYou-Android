package com.toyou.toyouandroid.presentation.fragment.record.network

data class DiaryCardNumResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DiaryCardNumResult
)

data class DiaryCardNumResult(
    val cardList: List<DiaryCardNum>
)

data class DiaryCardNum(
    val cardNum: Int,
    val date: String
)

data class DiaryCardPerDayResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DiaryCardPerDayResult
)

data class DiaryCardPerDayResult(
    val cardList: List<DiaryCardPerDay>
)

data class DiaryCardPerDay(
    val cardId: Int,
    val nickname: String,
    val emotion: String
)

data class DeleteDiaryCardResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)