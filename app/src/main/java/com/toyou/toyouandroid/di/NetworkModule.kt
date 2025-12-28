package com.toyou.toyouandroid.di

import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.onboarding.service.OnboardingService
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.data.mypage.service.MypageService
import com.toyou.toyouandroid.data.home.service.HomeService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideRetrofit(): Retrofit {
        return AuthNetworkModule.getClient()
    }

    @Provides
    @Singleton
    @NonAuthRetrofit
    fun provideNonAuthRetrofit(): Retrofit {
        return NetworkModule.getClient()
    }

    @Provides
    @Singleton
    fun provideOnboardingService(@NonAuthRetrofit retrofit: Retrofit): OnboardingService {
        return retrofit.create(OnboardingService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(@AuthRetrofit retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideEmotionService(@AuthRetrofit retrofit: Retrofit): EmotionService {
        return retrofit.create(EmotionService::class.java)
    }

    @Provides
    @Singleton
    fun provideMypageService(@AuthRetrofit retrofit: Retrofit): MypageService {
        return retrofit.create(MypageService::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeService(@AuthRetrofit retrofit: Retrofit): HomeService {
        return retrofit.create(HomeService::class.java)
    }
}