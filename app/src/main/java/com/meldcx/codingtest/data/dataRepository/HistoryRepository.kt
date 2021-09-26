package com.meldcx.codingtest.data.dataRepository

import com.meldcx.codingtest.data.dataSource.local.HistoryDao
import com.meldcx.codingtest.data.models.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/*
* A simple repository to catch data from AppDatabase
* Simple have 4 suspend methods for create,read,update and delete
* */
class HistoryRepository @Inject constructor(private val historyDao: HistoryDao) {

    //To insert a historyItem into the database
    suspend fun  insertHistoryData(historyItem: HistoryEntity) = historyDao.insert(historyItem)

    //To delete a specific historyItem from database
    suspend fun  deleteHistoryData(historyItem: HistoryEntity) = historyDao.delete(historyItem)

    //To update a specific historyItem from database
    suspend fun  updateHistoryData(historyItem: HistoryEntity) = historyDao.update(historyItem)

    //To get all historyItems from the appDatabase
    suspend fun  fetchAllHistories():Flow<List<HistoryEntity>>{
        return  flow {
            emit(historyDao.getAllHistories())
        }.flowOn(Dispatchers.IO)
    }

    //To get specific historyItems which url contains the keyText
    suspend fun  fetchHistoriesWithKeywords(keyText:String):Flow<List<HistoryEntity>>{
        return  flow {
            emit(historyDao.findHistoryWithUrl(keyText))
        }.flowOn(Dispatchers.IO)
    }


}