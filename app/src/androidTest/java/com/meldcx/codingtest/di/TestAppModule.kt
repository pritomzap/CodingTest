package com.meldcx.codingtest.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.meldcx.codingtest.data.dataRepository.HistoryRepository
import com.meldcx.codingtest.data.dataSource.local.AppDataBase
import com.meldcx.codingtest.data.dataSource.local.HistoryDao
import com.meldcx.codingtest.ui.viewModels.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Named("test_db")
    fun provideInMemoryDb(@ApplicationContext context: Context) = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).allowMainThreadQueries().build()
}