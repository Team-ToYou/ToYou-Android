package com.toyou.core.data.di

import com.toyou.core.data.repository.CreateRepositoryImpl
import com.toyou.core.data.repository.FCMRepositoryImpl
import com.toyou.core.data.repository.HomeRepositoryImpl
import com.toyou.core.data.repository.NoticeRepositoryImpl
import com.toyou.core.data.repository.ProfileRepositoryImpl
import com.toyou.core.data.repository.RecordRepositoryImpl
import com.toyou.core.data.repository.SocialRepositoryImpl
import com.toyou.core.domain.repository.ICreateRepository
import com.toyou.core.domain.repository.IFCMRepository
import com.toyou.core.domain.repository.IHomeRepository
import com.toyou.core.domain.repository.INoticeRepository
import com.toyou.core.domain.repository.IProfileRepository
import com.toyou.core.domain.repository.IRecordRepository
import com.toyou.core.domain.repository.ISocialRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCreateRepository(impl: CreateRepositoryImpl): ICreateRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): IHomeRepository

    @Binds
    @Singleton
    abstract fun bindNoticeRepository(impl: NoticeRepositoryImpl): INoticeRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): IProfileRepository

    @Binds
    @Singleton
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): IRecordRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(impl: SocialRepositoryImpl): ISocialRepository

    @Binds
    @Singleton
    abstract fun bindFCMRepository(impl: FCMRepositoryImpl): IFCMRepository
}
