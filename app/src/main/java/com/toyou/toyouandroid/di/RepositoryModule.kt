package com.toyou.toyouandroid.di

import com.toyou.toyouandroid.domain.create.repository.CreateRepositoryImpl
import com.toyou.toyouandroid.domain.create.repository.ICreateRepository
import com.toyou.toyouandroid.domain.home.repository.HomeRepositoryImpl
import com.toyou.toyouandroid.domain.home.repository.IHomeRepository
import com.toyou.toyouandroid.domain.notice.INoticeRepository
import com.toyou.toyouandroid.domain.notice.NoticeRepositoryImpl
import com.toyou.toyouandroid.domain.profile.repository.IProfileRepository
import com.toyou.toyouandroid.domain.profile.repository.ProfileRepositoryImpl
import com.toyou.toyouandroid.domain.record.IRecordRepository
import com.toyou.toyouandroid.domain.record.RecordRepositoryImpl
import com.toyou.toyouandroid.domain.social.repostitory.ISocialRepository
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepositoryImpl
import com.toyou.toyouandroid.fcm.domain.FCMRepositoryImpl
import com.toyou.toyouandroid.fcm.domain.IFCMRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): IHomeRepository

    @Binds
    @Singleton
    abstract fun bindCreateRepository(impl: CreateRepositoryImpl): ICreateRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(impl: SocialRepositoryImpl): ISocialRepository

    @Binds
    @Singleton
    abstract fun bindNoticeRepository(impl: NoticeRepositoryImpl): INoticeRepository

    @Binds
    @Singleton
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): IRecordRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): IProfileRepository

    @Binds
    @Singleton
    abstract fun bindFCMRepository(impl: FCMRepositoryImpl): IFCMRepository
}
