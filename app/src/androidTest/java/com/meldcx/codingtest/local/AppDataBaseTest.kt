package com.meldcx.codingtest.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.meldcx.codingtest.data.dataSource.local.AppDataBase
import com.meldcx.codingtest.data.dataSource.local.HistoryDao
import com.meldcx.codingtest.data.models.HistoryEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@SmallTest
class AppDataBaseTest{
    companion object{
        private const val TAG = "AppDataBaseTest"
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: AppDataBase
    private lateinit var historyDao: HistoryDao

    @Before
    fun setup() {
        hiltRule.inject()
        historyDao = database.historyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertHistoryItem() = runBlockingTest {
        val item = HistoryEntity(
            id = 1,
            imagePath = "testImagePath_1",
            url = "testUrl_1"
        )
        historyDao.insert(item)
        val allItems = historyDao.getAllHistories()
        assertThat(allItems).contains(item)
    }

    @Test
    fun fetchAllHistoryItem() = runBlockingTest {
        val numberOfItems = 9
        val entryList = generateMultipleItems(numberOfItems)
        entryList.forEach {
            historyDao.insert(it)
        }
        val allItems = historyDao.getAllHistories()
        assertThat(allItems).hasSize(numberOfItems)
    }

    @Test
    fun deleteHistoryItem() = runBlockingTest {
        val numberOfItems = 9
        val entryList = generateMultipleItems(numberOfItems)
        entryList.forEach {
            historyDao.insert(it)
        }
        historyDao.delete(entryList.random())
        val allItems = historyDao.getAllHistories()
        assertThat(allItems).hasSize(numberOfItems-1)
    }

    @Test
    fun updateHistoryItem() = runBlockingTest {
        val item = HistoryEntity(
            id = 1,
            imagePath = "testImagePath_1",
            url = "testUrl_1"
        )
        historyDao.insert(item)
        item.url = "testUrl_update"
        historyDao.update(item)
        val allItems = historyDao.getAllHistories()
        assertThat(allItems).contains(item)
    }

    @Test
    fun searchHistoryItem() = runBlockingTest {
        val numberOfItems = 9
        val entryList = generateMultipleItems(numberOfItems)
        entryList.forEach {
            historyDao.insert(it)
        }
        val filteredItems = historyDao.findHistoryWithUrl("testUrl_8")
        assertThat(filteredItems).hasSize(1)
    }

    private fun generateMultipleItems(numberOfItems:Int):List<HistoryEntity>{
        val entryList = mutableListOf<HistoryEntity>()
        for (i in 0 until numberOfItems){
            entryList.add(
                HistoryEntity(
                    id = i,
                    imagePath = "testImagePath_${i}",
                    url = "testUrl_${i}"
                )
            )
        }
        return entryList
    }

}