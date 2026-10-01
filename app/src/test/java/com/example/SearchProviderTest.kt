package com.example

import com.example.data.engine.*
import com.example.data.provider.*
import com.example.model.ExtractedFilters
import com.example.model.ProductItem
import com.example.model.ProviderStatus
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SearchProviderTest {

    // 1. API موفق (API Success)
    @Test
    fun testApiSuccessParsing() {
        val sampleJson = """
            {
                "results": [
                    {
                        "id": "101",
                        "title": "تویوتا کرولا کراس هیبرید ۲۰۲۵",
                        "url": "https://example.com/car/101",
                        "price": 6800000000,
                        "seller": "نمایندگی مرکزی",
                        "location": "تبریز",
                        "source": "وب رسمی"
                    }
                ]
            }
        """.trimIndent()

        val provider = WebSearchProvider(apiKey = "TEST_VALID_KEY")
        val items = provider.parseRealSearchResults(sampleJson, "کرولا")

        assertEquals(1, items.size)
        val item = items.first()
        assertEquals("تویوتا کرولا کراس هیبرید ۲۰۲۵", item.title)
        assertEquals("https://example.com/car/101", item.sourceUrl)
        assertEquals(6800000000L, item.price)
        assertFalse(item.isDemo) // Real web result, not demo
        assertEquals("وب رسمی", item.source)
    }

    // 2. API بدون کلید (Missing API Key)
    @Test
    fun testApiWithoutKeyReportsHonestStatus() = runBlocking {
        val provider = WebSearchProvider(apiKey = null)
        val status = provider.getStatus()

        assertEquals(ProviderStatus.REQUIRES_API_KEY, status.status)
        assertTrue(status.statusMessage.contains("جستجوی آنلاین هنوز برای این نسخه فعال نشده است"))

        val results = provider.search("تویوتا")
        assertTrue(results.isEmpty()) // NEVER fabricates fake web results without key
    }

    // 3. API خطادار (Server Error 500)
    @Test
    fun testApiServerError() = runBlocking {
        val mockClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(500)
                    .message("Internal Server Error")
                    .body("{}".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val provider = WebSearchProvider(apiKey = "KEY_ABC", client = mockClient)
        try {
            provider.search("تویوتا")
            fail("Expected IOException on server 500 error")
        } catch (e: IOException) {
            val status = provider.getStatus()
            assertEquals(ProviderStatus.TEMPORARILY_UNAVAILABLE, status.status)
        }
    }

    // 4. پاسخ خالی (Empty response)
    @Test
    fun testApiEmptyResponse() {
        val sampleJson = """{ "results": [] }"""
        val provider = WebSearchProvider(apiKey = "KEY_ABC")
        val items = provider.parseRealSearchResults(sampleJson, "کوئیک")
        assertTrue(items.isEmpty())
    }

    // 5. نتیجه تکراری (Duplicate removal)
    @Test
    fun testDeduplicationEngine() {
        val item1 = ProductItem(
            id = "item_1",
            title = "تویوتا کرولا کراس هیبرید ۲۰۲۵",
            category = "خودرو",
            brand = "تویوتا",
            model = "کرولا کراس",
            price = 6800000000L,
            formattedPrice = "۶.۸ میلیارد",
            location = "تبریز",
            seller = "فروشگاه پارس",
            sellerType = "فروشگاه",
            imageUrl = "",
            specs = emptyMap(),
            overallScore = 90,
            matchScore = 90,
            priceScore = 90,
            valueScore = 90,
            qualityScore = 90,
            riskScore = 15,
            confidenceScore = 90,
            source = "سایت الف",
            sourceUrl = "https://example.com/item/1"
        )
        // Exact duplicate URL
        val item2 = item1.copy(id = "item_2", source = "سایت ب", sourceUrl = "https://example.com/item/1")
        // Near-identical title + same price + seller
        val item3 = item1.copy(id = "item_3", title = "تویوتا کرولا کراس هیبرید 2025", sourceUrl = "https://example2.com/item/3")

        val list = listOf(item1, item2, item3)
        val deduplicated = DeduplicationEngine.deduplicate(list)

        assertEquals(1, deduplicated.size)
    }

    // 6. قیمت نامعتبر (Invalid price handling)
    @Test
    fun testInvalidPriceValidation() {
        assertFalse(PriceNormalizer.isValidPrice(0))
        assertFalse(PriceNormalizer.isValidPrice(-50000))
        assertFalse(PriceNormalizer.isValidPrice(999_999_999_999_999L))
        assertTrue(PriceNormalizer.isValidPrice(6_850_000_000L))
        assertTrue(PriceNormalizer.isValidPrice(150_000_000L))

        val sampleJson = """
            {
                "results": [
                    {
                        "id": "1",
                        "title": "خودروی بدون قیمت",
                        "url": "https://example.com/car/1",
                        "price": -100
                    }
                ]
            }
        """.trimIndent()
        val provider = WebSearchProvider(apiKey = "KEY_ABC")
        val items = provider.parseRealSearchResults(sampleJson, "خودرو")
        assertEquals(1, items.size)
        assertEquals(0L, items.first().price) // normalized invalid price to 0
        assertEquals("تماس برای قیمت", items.first().formattedPrice)
    }

    // 7. URL نامعتبر (Invalid URL handling)
    @Test
    fun testInvalidUrlValidation() {
        assertFalse(UrlValidator.isValidWebUrl(null))
        assertFalse(UrlValidator.isValidWebUrl(""))
        assertFalse(UrlValidator.isValidWebUrl("not_a_url"))
        assertFalse(UrlValidator.isValidWebUrl("javascript:alert(1)"))
        assertFalse(UrlValidator.isValidWebUrl("http://"))
        assertTrue(UrlValidator.isValidWebUrl("https://bama.ir/car/detail"))
        assertTrue(UrlValidator.isValidWebUrl("http://divar.ir/v/123"))

        val sampleJson = """
            {
                "results": [
                    {
                        "id": "1",
                        "title": "آگهی بدون لینک معتبر",
                        "url": "fake_url_here",
                        "price": 1000000
                    }
                ]
            }
        """.trimIndent()
        val provider = WebSearchProvider(apiKey = "KEY_ABC")
        val items = provider.parseRealSearchResults(sampleJson, "کالا")
        assertTrue(items.isEmpty()) // Rejects items with invalid URLs
    }

    // 8. Timeout (Timeout handling)
    @Test
    fun testTimeoutHandling() = runBlocking {
        val mockClient = OkHttpClient.Builder()
            .addInterceptor {
                throw SocketTimeoutException("Connection timed out")
            }
            .build()

        val provider = WebSearchProvider(apiKey = "KEY_ABC", client = mockClient)
        try {
            provider.search("خودرو")
            fail("Expected SocketTimeoutException")
        } catch (e: SocketTimeoutException) {
            val status = provider.getStatus()
            assertEquals(ProviderStatus.TEMPORARILY_UNAVAILABLE, status.status)
            assertTrue(status.statusMessage.contains("Timeout") || status.statusMessage.contains("پایان رسید"))
        }
    }

    // 9. Rate Limit (HTTP 429 handling)
    @Test
    fun testRateLimitHandling() = runBlocking {
        val mockClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(429)
                    .message("Too Many Requests")
                    .body("{}".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val provider = WebSearchProvider(apiKey = "KEY_ABC", client = mockClient)
        try {
            provider.search("تویوتا")
            fail("Expected RateLimitException on 429")
        } catch (e: RateLimitException) {
            val status = provider.getStatus()
            assertEquals(ProviderStatus.TEMPORARILY_UNAVAILABLE, status.status)
            assertTrue(status.statusMessage.contains("Rate Limit") || status.statusMessage.contains("محدودیت"))
        }
    }

    // 10. Price Analysis sufficiency tests (Section 5)
    @Test
    fun testPriceAnalysisSufficiency() {
        val singleItem = listOf(
            ProductItem(
                id = "1", title = "تکی", category = "", brand = "", model = "",
                price = 1000000, formattedPrice = "۱ میلیون", location = "", seller = "", sellerType = "",
                imageUrl = "", specs = emptyMap(), overallScore = 80, matchScore = 80, priceScore = 80,
                valueScore = 80, qualityScore = 80, riskScore = 20, confidenceScore = 80, source = "", sourceUrl = ""
            )
        )
        val insufficientResult = PriceAnalysisEngine.calculate(singleItem)
        assertFalse(insufficientResult.hasSufficientData)
        assertEquals("اطلاعات کافی برای تحلیل قیمت وجود ندارد.", insufficientResult.message)

        val multipleItems = singleItem + singleItem.map { it.copy(id = "2", price = 1200000) }
        val sufficientResult = PriceAnalysisEngine.calculate(multipleItems)
        assertTrue(sufficientResult.hasSufficientData)
        assertEquals(2, sufficientResult.count)
        assertEquals(1000000L, sufficientResult.minPrice)
        assertEquals(1200000L, sufficientResult.maxPrice)
        assertEquals(1100000L, sufficientResult.averagePrice)
    }

    // 11. Evidence-Based Risk Analysis tests (Section 6)
    @Test
    fun testEvidenceBasedRiskAnalysis() {
        val normalCar = ProductItem(
            id = "c1", title = "تویوتا کرولا کراس ۲۰۲۵", category = "خودرو", brand = "تویوتا", model = "کرولا",
            price = 6800000000L, formattedPrice = "", location = "تبریز", seller = "نمایندگی", sellerType = "نمایندگی",
            imageUrl = "", specs = mapOf("کارکرد" to "۲۵۰۰", "گیربکس" to "اتوماتیک", "بدنه" to "سالم"),
            overallScore = 90, matchScore = 90, priceScore = 90, valueScore = 90, qualityScore = 90,
            riskScore = 15, confidenceScore = 90, source = "", sourceUrl = ""
        )
        val cluster = listOf(
            normalCar,
            normalCar.copy(id = "c2", price = 6900000000L),
            normalCar.copy(id = "c3", price = 6700000000L)
        )

        // Normal car risk should be low
        val (riskNormal, warningsNormal) = EvidenceBasedRiskAnalyzer.analyze(normalCar, cluster)
        assertTrue(riskNormal <= 25)
        assertTrue(warningsNormal.isEmpty())

        // Severe price outlier (< 70% of avg) should add factual warning
        val cheapSuspicious = normalCar.copy(id = "c4", price = 4000000000L)
        val (riskCheap, warningsCheap) = EvidenceBasedRiskAnalyzer.analyze(cheapSuspicious, cluster)
        assertTrue(riskCheap > riskNormal)
        assertTrue(warningsCheap.any { it.contains("پایین‌تر از میانگین") })

        // Prepayment mentioned should trigger factual warning
        val prepaymentItem = normalCar.copy(specs = mapOf("توضیح" to "ارسال خودرو فقط بعد از واریز بیعانه ۵۰ میلیونی"))
        val (riskPrepay, warningsPrepay) = EvidenceBasedRiskAnalyzer.analyze(prepaymentItem, cluster)
        assertTrue(warningsPrepay.any { it.contains("بیعانه") })
    }
}
