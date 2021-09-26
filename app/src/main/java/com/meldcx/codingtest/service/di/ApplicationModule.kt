package com.meldcx.codingtest.service.di

import android.content.Context
import com.meldcx.codingtest.data.dataRepository.HistoryRepository
import com.meldcx.codingtest.data.dataRepository.sharedPreference.PreferenceHelper
import com.meldcx.codingtest.data.dataRepository.sharedPreference.PreferenceHelperImpl
import com.meldcx.codingtest.data.dataSource.local.AppDataBase
import com.meldcx.codingtest.data.dataSource.local.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
* Hilt DI application module
* Creates once in a lifetime of an app, until it gets destryed
* */
@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun providesHistoryDao(@ApplicationContext appContext:Context):HistoryDao = AppDataBase.getInstance(appContext).historyDao()

    @Provides
    @Singleton
    fun provideHistoryRepository(dao: HistoryDao) = HistoryRepository(dao)

    //SharedPreference module injection
    //we can inject it in any activiy or fragment by using @Inject lateint var preferenceHelper:PreferenceHelper
    //though i have't been used it anywhere
    @Singleton
    @Provides
    fun providePreferenceHelper(@ApplicationContext appContext:Context): PreferenceHelper = PreferenceHelperImpl(appContext)
}