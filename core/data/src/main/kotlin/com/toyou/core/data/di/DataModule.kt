package com.toyou.core.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // Repository bindings will be added here during migration
    // Example:
    // @Binds
    // @Singleton
    // abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}
