package com.toyou.toyouandroid.di

import com.toyou.toyouandroid.data.onboarding.service.OnboardingService
import com.toyou.toyouandroid.domain.profile.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideProfileRepository(
        onboardingService: OnboardingService
    ): ProfileRepository {
        return ProfileRepository(onboardingService)
    }
}