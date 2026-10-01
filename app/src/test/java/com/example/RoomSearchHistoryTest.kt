package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ai.AiAgentEngine
import com.example.data.local.AppDatabase
import com.example.data.local.SearchHistoryDao
import com.example.data.local.SearchHistoryEntity
import com.example.data.local.SearchHistoryRepository
import com.example.model.ExtractedFilters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomSearchHistoryTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: SearchHistoryDao
    private lateinit var repository: SearchHistoryRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.searchHistoryDao()
        repository = SearchHistoryRepository(dao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveVehicleSearchHistory() = runBlocking {
        val vehicleQuery = "تویوتا کرولا کراس ۲۰۲۵ تبریز"
        val vehicleFilters = ExtractedFilters(
            brand = "تویوتا",
            model = "کرولا کراس",
            year = 2025,
            location = "تبریز",
            priceMax = 7_000_000_000L,
            rawQuery = vehicleQuery,
            category = "خودرو"
        )

        repository.recordSearch(
            query = vehicleQuery,
            searchType = "VEHICLE",
            resultCount = 5,
            filters = vehicleFilters
        )

        val historyList = repository.allHistory.first()
        assertEquals(1, historyList.size)
        val entry = historyList[0]
        assertEquals(vehicleQuery, entry.query)
        assertEquals("VEHICLE", entry.searchType)
        assertEquals(5, entry.resultCount)
        assertEquals("تویوتا", entry.brand)
        assertEquals("کرولا کراس", entry.model)
        assertEquals("تبریز", entry.location)
        assertEquals(7_000_000_000L, entry.maxPrice)
        assertNotNull(entry.filterSummary)
        assertTrue(entry.filterSummary!!.contains("تویوتا"))
    }

    @Test
    fun testInsertItemAndVoiceSearchHistory() = runBlocking {
        // Product search
        repository.recordSearch(
            query = "کفش نایک زوم اورجینال",
            searchType = "PRODUCT",
            resultCount = 3
        )

        // Voice search for a vehicle
        repository.recordSearch(
            query = "یه پژو ۲۰۷ دنده‌ای تمیز می‌خوام",
            searchType = "VOICE",
            resultCount = 8
        )

        // Camera scan
        repository.recordSearch(
            query = "جستجوی بصری: گوشی سامسونگ",
            searchType = "CAMERA",
            resultCount = 4
        )

        val allHistory = repository.allHistory.first()
        assertEquals(3, allHistory.size)

        // Filter by type
        val vehicleOnly = repository.getHistoryByType("VOICE").first()
        assertEquals(1, vehicleOnly.size)
        assertEquals("یه پژو ۲۰۷ دنده‌ای تمیز می‌خوام", vehicleOnly[0].query)

        val cameraOnly = repository.getHistoryByType("CAMERA").first()
        assertEquals(1, cameraOnly.size)
        assertEquals("جستجوی بصری: گوشی سامسونگ", cameraOnly[0].query)
    }

    @Test
    fun testDeleteHistoryItemById() = runBlocking {
        repository.recordSearch("آیفون ۱۶ پرومکس", "PRODUCT", 2)
        repository.recordSearch("هیوندای سانتافه", "VEHICLE", 6)

        val initialList = repository.allHistory.first()
        assertEquals(2, initialList.size)

        val itemToDelete = initialList.first { it.query == "آیفون ۱۶ پرومکس" }
        repository.deleteHistoryItem(itemToDelete.id)

        val updatedList = repository.allHistory.first()
        assertEquals(1, updatedList.size)
        assertEquals("هیوندای سانتافه", updatedList[0].query)
    }

    @Test
    fun testClearAllHistory() = runBlocking {
        repository.recordSearch("لپ‌تاپ لنوو", "PRODUCT", 4)
        repository.recordSearch("ماشین بنز c200", "VEHICLE", 2)

        val beforeClear = repository.allHistory.first()
        assertEquals(2, beforeClear.size)

        repository.clearAllHistory()

        val afterClear = repository.allHistory.first()
        assertTrue(afterClear.isEmpty())
        assertEquals(0, dao.getCount())
    }

    @Test
    fun testDeduplicationInHistoryFloatsToTop() = runBlocking {
        repository.recordSearch("تویوتا کرولا", "VEHICLE", 2)
        repository.recordSearch("ساعت هوشمند", "PRODUCT", 5)

        // Re-searching same query should update its timestamp and move it to the front
        repository.recordSearch("تویوتا کرولا", "VEHICLE", 4)

        val list = repository.allHistory.first()
        assertEquals(2, list.size)
        assertEquals("تویوتا کرولا", list[0].query)
        assertEquals(4, list[0].resultCount)
    }

    @Test
    fun testVoiceSpeechToTextFilterExtraction() {
        val spokenVehicleQuery = "یه کرولا کراس هیبرید ۲۰۲۵ زیر ۷ میلیارد می‌خوام، ترجیحاً تبریز"
        val filters = AiAgentEngine.extractFilters(spokenVehicleQuery)

        assertEquals("تویوتا", filters.brand)
        assertEquals("کرولا کراس", filters.model)
        assertEquals(2025, filters.year)
        assertEquals(7_000_000_000L, filters.priceMax)
        assertEquals("تبریز", filters.location)
    }
}
