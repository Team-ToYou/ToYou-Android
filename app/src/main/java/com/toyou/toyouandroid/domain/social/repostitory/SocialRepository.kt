package com.toyou.toyouandroid.domain.social.repostitory

import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.network.RetrofitInstance

class SocialRepository {
    private val client = RetrofitInstance.getInstance().create(SocialService::class.java)

    suspend fun getFriendsData() = client.getFriends(1)
}