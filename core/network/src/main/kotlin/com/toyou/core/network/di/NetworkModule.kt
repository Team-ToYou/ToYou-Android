package com.toyou.core.network.di

import com.toyou.core.common.AppConstants
import com.toyou.core.network.api.AuthService
import com.toyou.core.network.api.CreateService
import com.toyou.core.network.api.EmotionService
import com.toyou.core.network.api.HomeService
import com.toyou.core.network.api.MypageService
import com.toyou.core.network.api.NoticeService
import com.toyou.core.network.api.OnboardingService
import com.toyou.core.network.api.RecordService
import com.toyou.core.network.api.SocialService
import com.toyou.core.network.api.FCMService
import com.toyou.core.network.interceptor.TokenAuthenticator
import com.toyou.core.network.interceptor.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NonAuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @NonAuthRetrofit
    fun provideNonAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(@AuthRetrofit okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.Network.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @NonAuthRetrofit
    fun provideNonAuthRetrofit(@NonAuthRetrofit okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.Network.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(@NonAuthRetrofit retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideCreateService(@AuthRetrofit retrofit: Retrofit): CreateService {
        return retrofit.create(CreateService::class.java)
    }

    @Provides
    @Singleton
    fun provideEmotionService(@AuthRetrofit retrofit: Retrofit): EmotionService {
        return retrofit.create(EmotionService::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeService(@AuthRetrofit retrofit: Retrofit): HomeService {
        return retrofit.create(HomeService::class.java)
    }

    @Provides
    @Singleton
    fun provideMypageService(@AuthRetrofit retrofit: Retrofit): MypageService {
        return retrofit.create(MypageService::class.java)
    }

    @Provides
    @Singleton
    fun provideNoticeService(@AuthRetrofit retrofit: Retrofit): NoticeService {
        return retrofit.create(NoticeService::class.java)
    }

    @Provides
    @Singleton
    fun provideOnboardingService(@AuthRetrofit retrofit: Retrofit): OnboardingService {
        return retrofit.create(OnboardingService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecordService(@AuthRetrofit retrofit: Retrofit): RecordService {
        return retrofit.create(RecordService::class.java)
    }

    @Provides
    @Singleton
    fun provideSocialService(@AuthRetrofit retrofit: Retrofit): SocialService {
        return retrofit.create(SocialService::class.java)
    }

    @Provides
    @Singleton
    fun provideFCMService(@AuthRetrofit retrofit: Retrofit): FCMService {
        return retrofit.create(FCMService::class.java)
    }
}
