package com.toyou.core.network.model.record

data class DiaryCardResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DiaryCardResult
)

data class DiaryCardResult(
    val cardList: List<DiaryCard>
)

data class DiaryCard(
    val cardId: Int,
    val emotion: String?,
    val date: String
)

data class PatchDiaryCardResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PatchDiaryCardResult
)

data class PatchDiaryCardResult(
    val exposure: Boolean
)

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
