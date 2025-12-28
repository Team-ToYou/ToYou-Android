package com.toyou.core.domain.repository

import com.toyou.core.domain.model.Friend

interface SocialRepository {
    suspend fun getFriends(): Result<List<Friend>>
    suspend fun searchFriend(nickname: String): Result<Friend?>
    suspend fun sendFriendRequest(userId: Long): Result<Unit>
    suspend fun acceptFriendRequest(userId: Long): Result<Unit>
    suspend fun rejectFriendRequest(userId: Long): Result<Unit>
    suspend fun deleteFriend(userId: Long): Result<Unit>
}
