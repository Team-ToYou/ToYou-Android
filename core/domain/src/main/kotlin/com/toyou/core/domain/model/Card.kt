package com.toyou.core.domain.model

data class Card(
    val id: Long,
    val date: String,
    val receiver: String,
    val emotion: String,
    val exposure: Boolean,
    val questions: List<Question>
)

data class Question(
    val id: Long,
    val content: String,
    val type: String,
    val questioner: String,
    val answer: String,
    val options: List<String>?
)
