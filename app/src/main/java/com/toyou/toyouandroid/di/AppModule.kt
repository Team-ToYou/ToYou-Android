package com.toyou.toyouandroid.di

import com.toyou.core.datastore.TokenManager
import com.toyou.toyouandroid.utils.TokenManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindTokenManager(impl: TokenManagerImpl): TokenManager
}