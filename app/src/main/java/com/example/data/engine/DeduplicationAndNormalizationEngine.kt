package com.example.data.engine

import com.example.model.ExtractedFilters
import com.example.model.PriceAnalysisResult
import com.example.model.ProductItem
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

object UrlValidator {
    /**
     * Validates that the URL is a legitimate public web address (http or https with valid host).
     */
    fun isValidWebUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            val uri = URI(url.trim())
            val scheme = uri.scheme?.lowercase()
            val host = uri.host
            (scheme == "http" || scheme == "https") && !host.isNullOrBlank() && host.contains(".")
        } catch (e: Exception) {
            false
        }
    }
}

object PriceNormalizer {
    /**
     * Normalizes and cleans price values. Rejects negative or absurd numbers.
     */
    fun isValidPrice(price: Long): Boolean {
        // Price must be positive and realistically under 5,000 Billion Tomans
        return price in 1_000..5_000_000_000_000L
    }

    fun formatToman(price: Long): String {
        return when {
            price >= 1_000_000_000L -> {
                val b = price / 1_000_000_000.0
                if (b == b.toLong().toDouble()) "${b.toLong()} میلیارد تومان"
                else "%.2f میلیارد تومان".format(b)
            }
            price >= 1_000_000L -> {
                val m = price / 1_000_000.0
                if (m == m.toLong().toDouble()) "${m.toLong()} میلیون تومان"
                else "%.1f میلیون تومان".format(m)
            }
            price > 0 -> {
                "%,d تومان".format(price)
            }
            else -> "قیمت توافقی / نامشخص"
        }
    }
}

object DeduplicationEngine {
    /**
     * Normalizes text for Persian matching (replaces Arabic characters, removes extra spaces).
     */
    fun normalizeText(input: String): String {
        return input.trim()
            .replace('ي', 'ی')
            .replace('ك', 'ک')
            .replace('\u200C', ' ') // half space to space
            .replace(Regex("\\s+"), " ")
            .lowercase()
    }

    /**
     * Deduplicates search results from multiple sources.
     * Identifies exact URL duplicates, near-identical titles with same price, or same seller.
     */
    fun deduplicate(items: List<ProductItem>): List<ProductItem> {
        val seenUrls = mutableSetOf<String>()
        val seenFingerprints = mutableSetOf<String>()
        val result = mutableListOf<ProductItem>()

        for (item in items) {
            // Check URL duplication
            if (UrlValidator.isValidWebUrl(item.sourceUrl)) {
                val cleanUrl = item.sourceUrl.trim().lowercase().removeSuffix("/")
                if (seenUrls.contains(cleanUrl)) {
                    continue
                }
                seenUrls.add(cleanUrl)
            }

            // Check Content fingerprint (Normalized Brand + Model + Year + Seller + Price)
            val normalizedTitle = normalizeText(item.title)
            val fingerprint = "${item.brand}_${item.model}_${item.year ?: 0}_${item.price}_${normalizeText(item.seller)}"
            val shortFingerprint = "${normalizedTitle.take(30)}_${item.price}"

            if (seenFingerprints.contains(fingerprint) || seenFingerprints.contains(shortFingerprint)) {
                continue
            }
            seenFingerprints.add(fingerprint)
            seenFingerprints.add(shortFingerprint)

            result.add(item)
        }
        return result
    }
}

object EvidenceBasedRiskAnalyzer {
    /**
     * Analyzes listing risk STRICTLY based on observable factual evidence.
     * Never falsely accuses or assumes without evidence.
     */
    fun analyze(
        item: ProductItem,
        clusterItems: List<ProductItem>
    ): Pair<Int, List<String>> {
        val warnings = mutableListOf<String>()
        var calculatedRisk = 15 // baseline low risk for normal listings

        // 1. Check price deviation if comparable items exist
        val validPrices = clusterItems.map { it.price }.filter { PriceNormalizer.isValidPrice(it) }
        if (validPrices.size >= 3) {
            val avgPrice = validPrices.average()
            if (item.price > 0 && item.price < avgPrice * 0.70) {
                val diffPercent = (((avgPrice - item.price) / avgPrice) * 100).toInt()
                calculatedRisk += 35
                warnings.add("قیمت این آگهی حدود $diffPercent٪ پایین‌تر از میانگین بازار است؛ نیاز به بررسی دقیق کارشناسی و تایید حضوری دارد.")
            }
        }

        // 2. Incomplete or vague specifications
        if (item.specs.isEmpty() || item.specs.size < 3) {
            calculatedRisk += 15
            warnings.add("اطلاعات و مشخصات فنی آگهی به صورت کامل ثبت نشده است.")
        }

        // 3. Prepayment or unverified seller patterns
        val allText = "${item.title} ${item.specs.values.joinToString(" ")}".lowercase()
        if (allText.contains("بیعانه") || allText.contains("کارت به کارت") || allText.contains("ارسال به شرط بیعانه")) {
            calculatedRisk += 40
            warnings.add("در متن آگهی به پرداخت بیعانه قبل از رویت اشاره شده است؛ پلیس فتا پرداخت هرگونه بیعانه را منع می‌کند.")
        }

        // 4. Model/Year discrepancies
        if (item.year != null && item.year < 2015 && item.title.contains("۲۰۲۵")) {
            calculatedRisk += 30
            warnings.add("تناقض در سال مدل (عنوان سال ۲۰۲۵ ذکر شده اما مشخصات سال متفاوتی را نشان می‌دهد).")
        }

        // Cap risk between 5 and 95
        val finalRisk = calculatedRisk.coerceIn(5, 95)
        return finalRisk to warnings
    }
}

object PriceAnalysisEngine {
    /**
     * Computes market statistics ONLY when sufficient valid real data exists (at least 2 items).
     */
    fun calculate(items: List<ProductItem>): PriceAnalysisResult {
        val validPrices = items.map { it.price }.filter { PriceNormalizer.isValidPrice(it) }.sorted()
        if (validPrices.size < 2) {
            return PriceAnalysisResult(
                hasSufficientData = false,
                count = validPrices.size,
                message = "اطلاعات کافی برای تحلیل قیمت وجود ندارد."
            )
        }

        val min = validPrices.first()
        val max = validPrices.last()
        val avg = validPrices.average().toLong()
        val median = if (validPrices.size % 2 == 1) {
            validPrices[validPrices.size / 2]
        } else {
            (validPrices[validPrices.size / 2 - 1] + validPrices[validPrices.size / 2]) / 2
        }

        val minText = PriceNormalizer.formatToman(min)
        val maxText = PriceNormalizer.formatToman(max)
        val avgText = PriceNormalizer.formatToman(avg)

        return PriceAnalysisResult(
            hasSufficientData = true,
            count = validPrices.size,
            averagePrice = avg,
            medianPrice = median,
            minPrice = min,
            maxPrice = max,
            formattedAverage = avgText,
            formattedMin = minText,
            formattedMax = maxText,
            priceRangeText = "از $minText تا $maxText",
            message = "تحلیل بر اساس ${validPrices.size} آگهی همتا محاسبه گردید."
        )
    }
}

object ScoreEngine {
    /**
     * Calculates scores only when data is sufficient.
     */
    fun computeMatchScore(item: ProductItem, filters: ExtractedFilters?): Int {
        if (filters == null) return item.matchScore
        var score = 70
        filters.brand?.let { b ->
            if (item.brand.contains(b, ignoreCase = true) || item.title.contains(b, ignoreCase = true)) score += 10
        }
        filters.model?.let { m ->
            if (item.model.contains(m, ignoreCase = true) || item.title.contains(m, ignoreCase = true)) score += 10
        }
        filters.priceMax?.let { pMax ->
            if (item.price in 1..pMax) score += 10
        }
        return score.coerceIn(40, 99)
    }
}

class SearchCacheManager(private val ttlMillis: Long = 15 * 60 * 1000L) {
    private data class CacheEntry(
        val timestamp: Long,
        val items: List<ProductItem>,
        val lastCheckedLabel: String
    )

    private val cache = ConcurrentHashMap<String, CacheEntry>()

    fun get(query: String, filterKey: String): Pair<List<ProductItem>, String>? {
        val key = makeKey(query, filterKey)
        val entry = cache[key] ?: return null
        val now = System.currentTimeMillis()
        if (now - entry.timestamp > ttlMillis) {
            cache.remove(key)
            return null
        }
        return entry.items to entry.lastCheckedLabel
    }

    fun put(query: String, filterKey: String, items: List<ProductItem>, timeLabel: String) {
        val key = makeKey(query, filterKey)
        cache[key] = CacheEntry(System.currentTimeMillis(), items, timeLabel)
    }

    fun clear() {
        cache.clear()
    }

    private fun makeKey(query: String, filterKey: String): String {
        return "${DeduplicationEngine.normalizeText(query)}__$filterKey"
    }
}
