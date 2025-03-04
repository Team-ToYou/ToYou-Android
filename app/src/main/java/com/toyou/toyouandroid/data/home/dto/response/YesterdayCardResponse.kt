package com.toyou.toyouandroid.data.home.dto.response

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class YesterdayCardResponse(
    @SerializedName("yesterday") val yesterday : List<YesterdayCard>
)

data class YesterdayCard(
    @SerializedName("cardId") val cardId : Long,
    @SerializedName("cardContent") val cardContent : YesterdayCardContent

)

data class YesterdayCardContent(
    @SerializedName("date") val date : LocalDate,
    @SerializedName("receiver") val receiver : String,
    @SerializedName("emotion") val emotion : String,
    @SerializedName("exposure") val exposure : Boolean,
    @SerializedName("questionList") val questionList : YesterdayCardQuestionList
)

data class YesterdayCardQuestionList(
    @SerializedName("questionId") val id : Long,
    @SerializedName("content") val content : String,
    @SerializedName("questionType") val type: String,
    @SerializedName("questioner") val questioner : String,
    @SerializedName("answer") val answer : String,
    @SerializedName("answerOption") val options : List<String>?,
)