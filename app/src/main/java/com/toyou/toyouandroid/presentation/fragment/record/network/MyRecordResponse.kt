package com.toyou.toyouandroid.presentation.fragment.record.network

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