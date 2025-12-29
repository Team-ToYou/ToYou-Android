package com.toyou.core.domain.model

data class CardModel(
    val message: String,
    val fromWho: String,
    val questionType: Int,
    var isButtonSelected: Boolean = false,
    val id: Long
)

data class CardShortModel(
    val message: String,
    val fromWho: String,
    val questionType: Int,
    var isButtonSelected: Boolean = false,
    val id: Long
)

data class ChooseModel(
    val message: String,
    val fromWho: String,
    val options: List<String>,
    val type: Int,
    var isButtonSelected: Boolean = false,
    val id: Long,
)

data class PreviewCardModel(
    val question: String,
    var answer: String,
    val type: Int,
    val fromWho: String,
    val options: List<String>?,
    val id: Long,
)

data class PreviewChooseModel(
    val message: String,
    val fromWho: String,
    val options: List<String>,
    val type: Int,
    var answer: String
)

data class FriendListModel(
    val id: Long,
    val name: String,
    val message: String?,
    val emotion: Int?
)
