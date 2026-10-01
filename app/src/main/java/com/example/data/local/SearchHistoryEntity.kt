package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing local search history for items, vehicles, voice queries, and camera scans.
 */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val searchType: String, // "ALL", "PRODUCT", "VEHICLE", "VOICE", "CAMERA"
    val timestamp: Long = System.currentTimeMillis(),
    val resultCount: Int = 0,
    val filterSummary: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val location: String? = null,
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
    val formattedPrice: String? = null
)
