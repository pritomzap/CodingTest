package com.meldcx.codingtest.data.dataSource.local

import androidx.room.*
import com.meldcx.codingtest.data.models.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    suspend fun getAllHistories() : List<HistoryEntity>

    @Query("SELECT * FROM history WHERE url LIKE '%' || :search || '%'")
    suspend fun findHistoryWithUrl(search: String): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity)

    @Delete
    suspend fun delete(item: HistoryEntity)

    @Update
    suspend fun update(item: HistoryEntity)
}