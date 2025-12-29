package com.toyou.core.datastore

interface TokenManager {
    suspend fun refreshTokenSuspend(): Result<String>
    fun refreshToken(onSuccess: (String) -> Unit, onFailure: () -> Unit)
}
