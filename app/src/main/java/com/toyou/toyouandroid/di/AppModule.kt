package com.toyou.toyouandroid.di

import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideNavigator(activity: FragmentActivity): FragmentNavigator {
        return NavigatorImpl(activity)
    }
}
