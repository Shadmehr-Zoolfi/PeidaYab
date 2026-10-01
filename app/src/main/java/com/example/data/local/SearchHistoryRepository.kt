package com.example.data.local

import com.example.model.ExtractedFilters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SearchHistoryRepository(
    private val dao: SearchHistoryDao
) {
    val allHistory: Flow<List<SearchHistoryEntity>> = dao.getAllHistory()
    val recentHistory: Flow<List<SearchHistoryEntity>> = dao.getRecentHistory(30)

    fun getHistoryByType(type: String): Flow<List<SearchHistoryEntity>> = dao.getHistoryByType(type)

    suspend fun recordSearch(
        query: String,
        searchType: String,
        resultCount: Int,
        filters: ExtractedFilters? = null
    ): Long = withContext(Dispatchers.IO) {
        val q = query.trim()
        if (q.isBlank()) return@withContext -1L

        // Remove existing identical query so the new one floats to top
        dao.deleteByQuery(q)

        val summary = filters?.let { f ->
            listOfNotNull(
                f.brand,
                f.model,
                f.year?.let { "مدل $it" },
                f.location,
                f.priceMax?.let { "حداکثر $it تومان" }
            ).joinToString(" | ").ifBlank { null }
        }

        val entry = SearchHistoryEntity(
            query = q,
            searchType = searchType,
            timestamp = System.currentTimeMillis(),
            resultCount = resultCount,
            filterSummary = summary,
            brand = filters?.brand,
            model = filters?.model,
            location = filters?.location,
            minPrice = null,
            maxPrice = filters?.priceMax
        )

        dao.insert(entry)
    }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        dao.clearAll()
    }
}
