package com.toyou.toyouandroid.di

import com.toyou.toyouandroid.data.create.service.CreateService
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.data.home.service.HomeService
import com.toyou.toyouandroid.data.mypage.service.MypageService
import com.toyou.toyouandroid.data.notice.service.NoticeService
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.onboarding.service.OnboardingService
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.fcm.service.FCMService
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

    @Provides
    @Singleton
    fun provideCreateService(@AuthRetrofit retrofit: Retrofit): CreateService {
        return retrofit.create(CreateService::class.java)
    }

    @Provides
    @Singleton
    fun provideSocialService(@AuthRetrofit retrofit: Retrofit): SocialService {
        return retrofit.create(SocialService::class.java)
    }

    @Provides
    @Singleton
    fun provideNoticeService(@AuthRetrofit retrofit: Retrofit): NoticeService {
        return retrofit.create(NoticeService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecordService(@AuthRetrofit retrofit: Retrofit): RecordService {
        return retrofit.create(RecordService::class.java)
    }

    @Provides
    @Singleton
    fun provideFCMService(@AuthRetrofit retrofit: Retrofit): FCMService {
        return retrofit.create(FCMService::class.java)
    }
}