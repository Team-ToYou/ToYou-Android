package com.toyou.core.data.repository

import com.toyou.core.data.mapper.toDomain
import com.toyou.core.data.mapper.toDomainResult
import com.toyou.core.data.mapper.toDomainResultUnit
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FriendsList
import com.toyou.core.domain.model.QuestionRequest
import com.toyou.core.domain.model.SearchFriendResult
import com.toyou.core.domain.repository.ISocialRepository
import com.toyou.core.network.api.SocialService
import com.toyou.core.network.model.social.QuestionDto
import com.toyou.core.network.model.social.RequestFriend
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val socialService: SocialService
) : ISocialRepository {

    override suspend fun getFriendsData(): DomainResult<FriendsList> {
        return socialService.getFriends().toDomainResult { it.toDomain() }
    }

    override suspend fun getSearchData(name: String): DomainResult<SearchFriendResult> {
        return socialService.getSearchFriend(name).toDomainResult { it.toDomain() }
    }

    override suspend fun patchApproveFriend(userId: Long): DomainResult<String> {
        return socialService.patchApprove(RequestFriend(userId)).toDomainResult { it.name }
    }

    override suspend fun deleteFriendData(userId: Long): DomainResult<Unit> {
        return socialService.deleteFriend(RequestFriend(userId)).toDomainResultUnit()
    }

    override suspend fun postRequest(userId: Long): DomainResult<String> {
        return socialService.postFriendRequest(RequestFriend(userId)).toDomainResult { it.name }
    }

    override suspend fun postQuestionData(question: QuestionRequest): DomainResult<String> {
        val dto = QuestionDto(
            targetId = question.targetId,
            content = question.content,
            type = question.type.value,
            anonymous = question.anonymous,
            options = question.options
        )
        return socialService.postQuestion(dto).toDomainResult { it.name }
    }
}
