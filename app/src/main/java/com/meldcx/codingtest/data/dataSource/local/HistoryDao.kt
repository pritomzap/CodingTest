package com.meldcx.codingtest.data.dataSource.local

import androidx.room.*
import com.meldcx.codingtest.data.models.HistoryEntity
import kotlinx.coroutines.flow.Flow

/*
* DAO interface for Room database Queries and operations
* */
@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    suspend fun getAllHistories() : List<HistoryEntity>

    //THis query is to get some specific items that matches with the keyword or search String
    @Query("SELECT * FROM history WHERE url LIKE '%' || :search || '%'")
    suspend fun findHistoryWithUrl(search: String): List<HistoryEntity>

    //OnConflict the previous data will be replaced by new data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity)

    @Delete
    suspend fun delete(item: HistoryEntity)

    @Update
    suspend fun update(item: HistoryEntity)
}