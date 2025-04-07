package com.toyou.toyouandroid.data.home.dto.response

import com.google.gson.annotations.SerializedName

data class YesterdayCardResponse(
    @SerializedName("cards") val yesterday: List<YesterdayCard>
)

data class YesterdayCard(
    @SerializedName("cardId") val cardId: Long,
    @SerializedName("cardContent") val cardContent: YesterdayCardContent
)

data class YesterdayCardContent(
    @SerializedName("date") val date: String,
    @SerializedName("receiver") val receiver: String,
    @SerializedName("emotion") val emotion: String,
    @SerializedName("exposure") val exposure: Boolean,
    @SerializedName("questionList") val questionList: List<YesterdayCardQuestion>
)

data class YesterdayCardQuestion(
    @SerializedName("questionId") val id: Long,
    @SerializedName("content") val content: String,
    @SerializedName("questionType") val type: String,
    @SerializedName("questioner") val questioner: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("answerOption") val options: List<String>?
)
