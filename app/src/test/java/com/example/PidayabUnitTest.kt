package com.example

import com.example.ai.AiAgentEngine
import com.example.data.MarketplaceDemoSearchProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class PidayabUnitTest {

    @Test
    fun testPersianNaturalLanguageExtraction() {
        val query = "یه کرولا کراس هیبرید مدل ۲۰۲۵ میخوام، زیر ۷ میلیارد، کارکرد پایین، ترجیحاً تبریز."
        val filters = AiAgentEngine.extractFilters(query)

        assertEquals("تویوتا", filters.brand)
        assertEquals("کرولا کراس", filters.model)
        assertEquals(2025, filters.year)
        assertEquals("تبریز", filters.location)
        assertEquals("هیبرید", filters.fuelType)
        assertNotNull(filters.priceMax)
        assertTrue(filters.priceMax!! <= 7_000_000_000L)
    }

    @Test
    fun testSearchCatalogResults() = runBlocking {
        val provider = MarketplaceDemoSearchProvider()
        val results = provider.search("کرولا کراس")

        assertTrue(results.isNotEmpty())
        val first = results.first()
        assertTrue(first.overallScore in 0..100)
        assertTrue(first.riskScore in 0..100)
        assertTrue(first.confidenceScore in 0..100)
        assertNotNull(first.riskLabel)
    }
}
