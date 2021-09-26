package com.meldcx.codingtest.data.dataRepository

import com.meldcx.codingtest.data.dataSource.local.HistoryDao
import com.meldcx.codingtest.data.models.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val historyDao: HistoryDao) {

    suspend fun  insertHistoryData(historyItem: HistoryEntity) = historyDao.insert(historyItem)

    suspend fun  deleteHistoryData(historyItem: HistoryEntity) = historyDao.delete(historyItem)

    suspend fun  updateHistoryData(historyItem: HistoryEntity) = historyDao.update(historyItem)

    suspend fun  fetchAllHistories():Flow<List<HistoryEntity>>{
        return  flow {
            emit(historyDao.getAllHistories())
        }.flowOn(Dispatchers.IO)
    }

    suspend fun  fetchHistoriesWithKeywords(keyText:String):Flow<List<HistoryEntity>>{
        return  flow {
            emit(historyDao.findHistoryWithUrl(keyText))
        }.flowOn(Dispatchers.IO)
    }


}