package com.toyou.core.data.mapper

import com.toyou.core.domain.model.Alarm
import com.toyou.core.domain.model.AlarmList
import com.toyou.core.domain.model.AlarmType
import com.toyou.core.domain.model.CardDetailInfo
import com.toyou.core.domain.model.CardExposure
import com.toyou.core.domain.model.CardPostResult
import com.toyou.core.domain.model.CreateQuestionInfo
import com.toyou.core.domain.model.CreateQuestionList
import com.toyou.core.domain.model.DiaryCard
import com.toyou.core.domain.model.DiaryCardList
import com.toyou.core.domain.model.DiaryCardNum
import com.toyou.core.domain.model.DiaryCardNumList
import com.toyou.core.domain.model.DiaryCardPerDay
import com.toyou.core.domain.model.DiaryCardPerDayList
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FcmTokenList
import com.toyou.core.domain.model.FriendInfo
import com.toyou.core.domain.model.FriendRequest
import com.toyou.core.domain.model.FriendRequestList
import com.toyou.core.domain.model.FriendStatus
import com.toyou.core.domain.model.FriendsList
import com.toyou.core.domain.model.HomeInfo
import com.toyou.core.domain.model.NicknameCheckResult
import com.toyou.core.domain.model.QuestionAnswer
import com.toyou.core.domain.model.SearchFriendResult
import com.toyou.core.domain.model.YesterdayCardInfo
import com.toyou.core.domain.model.YesterdayCardList
import com.toyou.core.network.model.BaseResponse
import com.toyou.core.network.model.create.AnswerPost
import com.toyou.core.network.model.create.HomeDto
import com.toyou.core.network.model.create.QuestionsDto
import com.toyou.core.network.model.fcm.GetToken
import com.toyou.core.network.model.home.CardDetail
import com.toyou.core.network.model.home.YesterdayCardResponse
import com.toyou.core.network.model.notice.AlarmResponse
import com.toyou.core.network.model.notice.FriendsRequestResponse
import com.toyou.core.network.model.onboarding.NicknameCheckResponse
import com.toyou.core.network.model.record.DiaryCardNumResponse
import com.toyou.core.network.model.record.DiaryCardPerDayResponse
import com.toyou.core.network.model.record.DiaryCardResponse
import com.toyou.core.network.model.record.PatchDiaryCardResponse
import com.toyou.core.network.model.social.FriendsDto
import com.toyou.core.network.model.social.SearchFriendDto
import retrofit2.Response

fun <T, R> BaseResponse<T>.toDomainResult(mapper: (T) -> R): DomainResult<R> {
    return if (isSuccess) {
        DomainResult.Success(mapper(result!!))
    } else {
        DomainResult.Error(code.toIntOrNull() ?: -1, message)
    }
}

fun <T> BaseResponse<T>.toDomainResultUnit(): DomainResult<Unit> {
    return if (isSuccess) {
        DomainResult.Success(Unit)
    } else {
        DomainResult.Error(code.toIntOrNull() ?: -1, message)
    }
}

fun <T, R> Response<T>.toDomainResult(
    mapper: (T) -> R
): DomainResult<R> {
    return if (isSuccessful && body() != null) {
        DomainResult.Success(mapper(body()!!))
    } else {
        DomainResult.Error(code(), message())
    }
}

fun <T> Response<T>.toDomainResultUnit(): DomainResult<Unit> {
    return if (isSuccessful) {
        DomainResult.Success(Unit)
    } else {
        DomainResult.Error(code(), message())
    }
}

// Home Mappers
fun CardDetail.toDomain(): CardDetailInfo = CardDetailInfo(
    date = date,
    receiver = receiver,
    emotion = emotion,
    exposure = exposure,
    questions = questions.map { q ->
        QuestionAnswer(
            id = q.id,
            content = q.content,
            type = q.type,
            questioner = q.questioner,
            answer = q.answer,
            options = q.options
        )
    }
)

fun YesterdayCardResponse.toDomain(): YesterdayCardList = YesterdayCardList(
    cards = yesterday.map { card ->
        YesterdayCardInfo(
            cardId = card.cardId,
            date = card.cardContent.date,
            receiver = card.cardContent.receiver,
            emotion = card.cardContent.emotion,
            exposure = card.cardContent.exposure,
            questions = card.cardContent.questionList.map { q ->
                QuestionAnswer(
                    id = q.id,
                    content = q.content,
                    type = q.type,
                    questioner = q.questioner,
                    answer = q.answer,
                    options = q.options
                )
            }
        )
    }
)

// Social Mappers
fun FriendsDto.toDomain(): FriendsList = FriendsList(
    friends = friends.map { f ->
        FriendInfo(
            id = f.userId,
            nickname = f.nickname,
            emotion = f.emotion,
            message = f.ment
        )
    }
)

fun SearchFriendDto.toDomain(): SearchFriendResult = SearchFriendResult(
    userId = userId,
    name = name,
    status = FriendStatus.fromString(status)
)

// Record Mappers
fun DiaryCardResponse.toDomain(): DiaryCardList = DiaryCardList(
    cards = result.cardList.map { c ->
        DiaryCard(
            cardId = c.cardId,
            emotion = c.emotion,
            date = c.date
        )
    }
)

fun DiaryCardNumResponse.toDomain(): DiaryCardNumList = DiaryCardNumList(
    cards = result.cardList.map { c ->
        DiaryCardNum(
            cardNum = c.cardNum,
            date = c.date
        )
    }
)

fun DiaryCardPerDayResponse.toDomain(): DiaryCardPerDayList = DiaryCardPerDayList(
    cards = result.cardList.map { c ->
        DiaryCardPerDay(
            cardId = c.cardId,
            nickname = c.nickname,
            emotion = c.emotion
        )
    }
)

fun PatchDiaryCardResponse.toDomain(): CardExposure = CardExposure(
    exposure = result.exposure
)

// Notice Mappers
fun AlarmResponse.toDomain(): AlarmList = AlarmList(
    alarms = result.alarmList.map { a ->
        Alarm(
            alarmId = a.alarmId,
            content = a.content,
            nickname = a.nickname,
            alarmType = AlarmType.fromString(a.alarmType)
        )
    }
)

fun FriendsRequestResponse.toDomain(): FriendRequestList = FriendRequestList(
    requests = result.friendsRequestList.map { r ->
        FriendRequest(
            userId = r.userId,
            nickname = r.nickname
        )
    }
)

// Profile Mappers
fun NicknameCheckResponse.toDomain(): NicknameCheckResult = NicknameCheckResult(
    exists = result.exists
)

// Create Mappers
fun QuestionsDto.toDomain(): CreateQuestionList = CreateQuestionList(
    questions = questions.map { q ->
        CreateQuestionInfo(
            id = q.id,
            content = q.content,
            type = q.type,
            questioner = q.questioner,
            options = q.options
        )
    }
)

fun HomeDto.toDomain(): HomeInfo = HomeInfo(
    nickname = nickname,
    emotion = emotion,
    questionCount = question,
    cardId = id,
    hasUncheckedAlarm = alarm
)

fun AnswerPost.toDomain(): CardPostResult = CardPostResult(
    cardId = id
)

// FCM Mappers
fun GetToken.toDomain(): FcmTokenList = FcmTokenList(
    tokens = tokens
)
