package com.toyou.toyouandroid.di

import com.toyou.core.datastore.TokenStorage
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        authService: AuthService,
        tokenStorage: TokenStorage
    ): TokenManager {
        return TokenManager(authService, tokenStorage)
    }
}