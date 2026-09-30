package com.example.model

data class HistoricalPricePoint(
    val dateLabel: String,
    val price: Long,
    val formattedPrice: String
)

data class ProductItem(
    val id: String,
    val title: String,
    val category: String,
    val brand: String,
    val model: String,
    val year: Int? = null,
    val price: Long,
    val formattedPrice: String,
    val location: String,
    val seller: String,
    val sellerType: String, // "شخصی", "فروشگاه", "نمایندگی رسمی"
    val imageUrl: String,
    val specs: Map<String, String>,
    // 0-100 Scores
    val overallScore: Int,
    val matchScore: Int,
    val priceScore: Int,
    val valueScore: Int,
    val qualityScore: Int,
    val riskScore: Int,
    val confidenceScore: Int,
    val riskFactors: List<String> = emptyList(),
    val pros: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val missingInfo: List<String> = emptyList(),
    val source: String,
    val sourceUrl: String,
    val isDemo: Boolean = true,
    val sourceVerificationNote: String = "اطلاعات با استانداردهای منبع اصلی تطبیق داده شد",
    val historicalPrices: List<HistoricalPricePoint> = emptyList(),
    val isBookmarked: Boolean = false,
    val isBestChoice: Boolean = false,
    val isBestPrice: Boolean = false,
    val isLowestRisk: Boolean = false,
    val isBestValue: Boolean = false,
    val isBestMatch: Boolean = false,
    val isLocalBest: Boolean = false
) {
    val riskLabel: String
        get() = when {
            riskScore <= 20 -> "ریسک بسیار پایین"
            riskScore <= 40 -> "ریسک پایین"
            riskScore <= 60 -> "ریسک متوسط"
            riskScore <= 80 -> "ریسک بالا"
            else -> "ریسک بسیار بالا"
        }
}

data class ExtractedFilters(
    val rawQuery: String = "",
    var category: String? = null,
    var brand: String? = null,
    var model: String? = null,
    var year: Int? = null,
    var priceMax: Long? = null,
    var mileageMax: Long? = null,
    var location: String? = null,
    var condition: String? = null,
    var fuelType: String? = null,
    var transmission: String? = null,
    var sellerType: String? = null,
    var riskPreference: String? = null
)

data class SearchAgentStep(
    val id: Int,
    val title: String,
    var isDone: Boolean = false,
    var isActive: Boolean = false
)

data class PriceAlert(
    val id: String,
    val title: String,
    val targetPrice: Long,
    val formattedTargetPrice: String,
    val currentMarketPrice: String,
    val category: String,
    val isActive: Boolean = true,
    val createdDate: String
)

data class ChatMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: String,
    val suggestedActions: List<String> = emptyList(),
    val referencedProductId: String? = null
)

data class UserPreferences(
    val city: String = "تبریز",
    val currency: String = "تومان",
    val maxRiskTolerance: Int = 30,
    val budgetMax: Long = 7000000000L,
    val isDarkMode: Boolean = true,
    val preferredBrands: List<String> = listOf("تویوتا", "هیوندای", "سامسونگ", "اپل", "نایک"),
    val excludedBrands: List<String> = emptyList()
)

data class AdminMetrics(
    val totalUsers: Int = 18450,
    val dailySearches: Int = 4280,
    val popularCategories: List<Pair<String, Int>> = listOf(
        "خودرو و وسایل نقلیه" to 1940,
        "کالای دیجیتال و موبایل" to 1120,
        "کفش و پوشاک" to 580,
        "ساعت و اکسسوری" to 340,
        "لوازم خانگی" to 300
    ),
    val popularQueries: List<String> = listOf(
        "کرولا کراس هیبرید ۲۰۲۵",
        "آیفون ۱۶ پرومکس",
        "کفش نایک زوم ایکس",
        "سامسونگ اس ۲۵ الترا",
        "پژو ۲۰۷ اتوماتیک صفر"
    ),
    val apiCallsCount: Int = 12950,
    val systemHealth: String = "پایدار و عالی (۹۹.۹٪)",
    val recentLogs: List<String> = listOf(
        "تکمیل استخراج هوشمند برای ۱۲۰ کاربر آنلاین",
        "بررسی ریسک و فیلتر آگهی‌های مشکوک به صورت برخط",
        "به‌روزرسانی موتور تحلیل قیمت و خوشه‌بندی بازار"
    )
)
