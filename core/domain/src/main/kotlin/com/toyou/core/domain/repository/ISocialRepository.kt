package com.toyou.core.domain.repository

import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.FriendsList
import com.toyou.core.domain.model.QuestionRequest
import com.toyou.core.domain.model.SearchFriendResult

interface ISocialRepository {
    suspend fun getFriendsData(): DomainResult<FriendsList>
    suspend fun getSearchData(name: String): DomainResult<SearchFriendResult>
    suspend fun patchApproveFriend(userId: Long): DomainResult<String>
    suspend fun deleteFriendData(userId: Long): DomainResult<Unit>
    suspend fun postRequest(userId: Long): DomainResult<String>
    suspend fun postQuestionData(question: QuestionRequest): DomainResult<String>
}
