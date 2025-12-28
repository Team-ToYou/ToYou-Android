package com.toyou.toyouandroid.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Repository들은 @Inject constructor와 @Singleton 어노테이션을 통해
 * Hilt가 자동으로 제공합니다.
 *
 * 적용된 Repository 목록:
 * - HomeRepository
 * - CreateRepository
 * - SocialRepository
 * - NoticeRepository
 * - RecordRepository
 * - ProfileRepository
 * - FCMRepository
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule