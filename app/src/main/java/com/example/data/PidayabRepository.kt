package com.example.data

import com.example.model.ExtractedFilters
import com.example.model.HistoricalPricePoint
import com.example.model.PriceAlert
import com.example.model.ProductItem
import com.example.model.UserPreferences

interface SearchProvider {
    val providerName: String
    val isLiveApi: Boolean
    suspend fun search(query: String, filters: ExtractedFilters? = null): List<ProductItem>
    suspend fun searchByImage(descriptionOrTag: String): List<ProductItem>
    suspend fun searchByVideo(videoTagOrUrl: String): List<ProductItem>
    suspend fun searchByFile(documentTitle: String): List<ProductItem>
}

class MarketplaceDemoSearchProvider : SearchProvider {
    override val providerName: String = "موتور کاوش آگهی‌های مجاز (حالت آزمایشی تاییدشده)"
    override val isLiveApi: Boolean = false

    private val allCatalog: List<ProductItem> = listOf(
        // Corolla Cross 1 - Best Choice, Local Tabriz, 6.85 Billion
        ProductItem(
            id = "toyota_corolla_cross_2025_tbz",
            title = "تویوتا کرولا کراس هیبرید مدل ۲۰۲۵ وارداتی",
            category = "خودرو و وسایل نقلیه",
            brand = "تویوتا",
            model = "کرولا کراس",
            year = 2025,
            price = 6850000000L,
            formattedPrice = "۶.۸۵ میلیارد تومان",
            location = "تبریز، ولیعصر",
            seller = "نمایندگی رسمی بازرگانی آذربایجان",
            sellerType = "نمایندگی رسمی",
            imageUrl = "https://images.unsplash.com/photo-1590362891991-f776e747a588?w=800&auto=format&fit=crop",
            specs = mapOf(
                "کارکرد" to "۲,۵۰۰ کیلومتر (در حد صفر)",
                "پیشرانه" to "۲.۰ لیتر بنزینی + موتور الکتریکی هیبرید",
                "گیربکس" to "E-CVT اتوماتیک هوشمند",
                "وضعیت بدنه" to "فاقد هرگونه رنگ‌شدگی یا تصادف (کارشناسی‌شده)",
                "گارانتی" to "۵ سال گارانتی کامل باتری و پیشرانه",
                "امکانات" to "کروز کنترل تطبیقی، رادار خط‌خوان، دوربین ۳۶۰، سقف پانوراما"
            ),
            overallScore = 95,
            matchScore = 98,
            priceScore = 93,
            valueScore = 96,
            qualityScore = 97,
            riskScore = 14, // 0-20 Very low risk
            confidenceScore = 94,
            pros = listOf(
                "تطابق فوق‌العاده با فیلترهای درخواستی شما",
                "قیمت در محدوده منصفانه و پایین‌تر از میانگین بازار",
                "برگه کارشناسی رسمی معتبر و تاییدشده",
                "حضور فیزیکی فروشنده و امکان بازدید حضوری در تبریز"
            ),
            warnings = listOf(
                "توصیه به کارشناسی مجدد رنگ و بدنه پیش از تسویه نهایی"
            ),
            riskFactors = listOf(
                "سطح ریسک: بسیار پایین (فروشنده باسابقه و تایید هویت‌شده)"
            ),
            source = "باما (Bama)",
            sourceUrl = "https://bama.ir/car/detail-corolla-cross-2025",
            isDemo = true,
            sourceVerificationNote = "مشخصات و قیمت با ۲ آگهی همتا در تبریز و تهران تطبیق داده شد.",
            historicalPrices = listOf(
                HistoricalPricePoint("۳۰ روز قبل", 7100000000L, "۷.۱ میلیارد"),
                HistoricalPricePoint("۱۵ روز قبل", 6950000000L, "۶.۹۵ میلیارد"),
                HistoricalPricePoint("امروز", 6850000000L, "۶.۸۵ میلیارد")
            ),
            isBestChoice = true,
            isBestMatch = true,
            isLocalBest = true,
            isLowestRisk = true
        ),

        // Corolla Cross 2 - Lowest Price, 6.69 Billion
        ProductItem(
            id = "toyota_corolla_cross_2025_esf",
            title = "تویوتا کرولا کراس هیبرید ۲۰۲۵ فول آپشن",
            category = "خودرو و وسایل نقلیه",
            brand = "تویوتا",
            model = "کرولا کراس",
            year = 2025,
            price = 6690000000L,
            formattedPrice = "۶.۶۹ میلیارد تومان",
            location = "اصفهان، مرداویج",
            seller = "اتومبیل پارسیان",
            sellerType = "فروشگاه",
            imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=800&auto=format&fit=crop",
            specs = mapOf(
                "کارکرد" to "۶,۰۰۰ کیلومتر",
                "پیشرانه" to "هیبرید تویوتا نسل پنجم",
                "گیربکس" to "اتوماتیک هوشمند",
                "وضعیت بدنه" to "یک لکه لیسه‌گیری جزئی روی سپر عقب",
                "گارانتی" to "۳ سال گارانتی شرکتی"
            ),
            overallScore = 91,
            matchScore = 92,
            priceScore = 98,
            valueScore = 94,
            qualityScore = 89,
            riskScore = 24, // Low risk
            confidenceScore = 88,
            pros = listOf(
                "ارزان‌ترین گزینه موجود در بازار با وضعیت فنی سالم",
                "ارزش خرید بسیار بالا نسبت به قیمت"
            ),
            warnings = listOf(
                "لکه لیسه‌گیری روی سپر عقب که در قیمت لحاظ شده است",
                "فاصله جغرافیایی نسبت به شهر شما (نیاز به هماهنگی حمل)"
            ),
            riskFactors = listOf(
                "ریسک پایین - فروشگاه دارای جواز کسب معتبر است"
            ),
            source = "دیوار (Divar)",
            sourceUrl = "https://divar.ir/v/corolla-cross-hybrid",
            isDemo = true,
            sourceVerificationNote = "قیمت نسبت به میانگین بازار حدود ۳٪ ارزان‌تر مشاهده شده است.",
            isBestPrice = true,
            isBestValue = true
        ),

        // Corolla Cross 3 - Suspicious / High Risk Example
        ProductItem(
            id = "toyota_corolla_cross_suspicious",
            title = "تویوتا کرولا کراس ۲۰۲۵ تحویل فوری به شرط بیعانه",
            category = "خودرو و وسایل نقلیه",
            brand = "تویوتا",
            model = "کرولا کراس",
            year = 2025,
            price = 5100000000L,
            formattedPrice = "۵.۱۰ میلیارد تومان (⚠️ غیرعادی پایین)",
            location = "تهران، پونک",
            seller = "شخصی (ثبت‌نام جدید)",
            sellerType = "شخصی",
            imageUrl = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=800&auto=format&fit=crop",
            specs = mapOf(
                "کارکرد" to "۰ کیلومتر",
                "پیشرانه" to "هیبرید",
                "توضیحات" to "فروش فوری به دلیل مهاجرت، ارسال با بیعانه"
            ),
            overallScore = 48,
            matchScore = 75,
            priceScore = 30,
            valueScore = 40,
            qualityScore = 42,
            riskScore = 78, // High risk
            confidenceScore = 89,
            pros = listOf(
                "قیمت پیشنهادی روی کاغذ بسیار وسوسه‌کننده است"
            ),
            warnings = listOf(
                "قیمت حدود ۲۵٪ پایین‌تر از کف قیمت واقعی بازار است!",
                "درخواست پرداخت بیعانه پیش از رویت خودرو",
                "عدم ارائه شماره شاسی، پلاک و برگه کارشناسی معتبر",
                "حساب کاربری فروشنده کمتر از ۴۸ ساعت پیش ایجاد شده است"
            ),
            riskFactors = listOf(
                "قیمت غیرعادی پایین نسبت به بازار",
                "درخواست مشکوک بیعانه",
                "نقص جدی در اسناد و مدارک فنی"
            ),
            missingInfo = listOf("شماره شاسی VIN", "برگه کارشناسی رنگ", "تصاویر واقعی از زوایای مختلف"),
            source = "شیپور (Sheypoor)",
            sourceUrl = "https://sheypoor.com/v/corolla-cross-cheap",
            isDemo = true,
            sourceVerificationNote = "سیستم تحلیل ریسک پیدایاب این آگهی را پرریسک ارزیابی کرده است."
        ),

        // Hyundai Santa Fe 2024
        ProductItem(
            id = "hyundai_santafe_2024",
            title = "هیوندای سانتافه هیبرید ۲۰۲۴ نیوفیس",
            category = "خودرو و وسایل نقلیه",
            brand = "هیوندای",
            model = "سانتافه",
            year = 2024,
            price = 7400000000L,
            formattedPrice = "۷.۴۰ میلیارد تومان",
            location = "تبریز، آبرسان",
            seller = "اتومبیل مهر تبریز",
            sellerType = "فروشگاه",
            imageUrl = "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&auto=format&fit=crop",
            specs = mapOf(
                "کارکرد" to "۸,۰۰۰ کیلومتر",
                "پیشرانه" to "۱.۶ توربو هیبرید",
                "سیستم انتقال" to "چهار چرخ محرک AWD",
                "وضعیت" to "فوق‌العاده تمیز بدون خط و خش"
            ),
            overallScore = 89,
            matchScore = 84,
            priceScore = 87,
            valueScore = 88,
            qualityScore = 93,
            riskScore = 18,
            confidenceScore = 92,
            pros = listOf("طراحی بسیار مدرن و جادار", "آپشن‌های کامل ایمنی و رادارهای هوشمند"),
            source = "باما (Bama)",
            sourceUrl = "https://bama.ir/car/santafe-2024",
            isDemo = true
        ),

        // iPhone 16 Pro Max
        ProductItem(
            id = "iphone_16_promax",
            title = "اپل آیفون ۱۶ پرو مکس ۲۵۶ گیگابایت Natural Titanium",
            category = "کالای دیجیتال و موبایل",
            brand = "اپل",
            model = "iPhone 16 Pro Max",
            year = 2024,
            price = 108500000L,
            formattedPrice = "۱۰۸.۵ میلیون تومان",
            location = "تهران، بازار چارسو",
            seller = "فروشگاه تخصصی سیب طلایی",
            sellerType = "فروشگاه",
            imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&auto=format&fit=crop",
            specs = mapOf(
                "حافظه داخلی" to "۲۵۶ گیگابایت",
                "پارت نامبر" to "ZAA (دو سیم‌کارت فیزیکی)",
                "وضعیت ریجستری" to "ریجسترشده قانونی در سامانه همتا",
                "سلامت باتری" to "۱۰۰٪ (آکبند نات اکتیو)",
                "گارانتی" to "۱۸ ماه گارانتی رسمی شرکتی"
            ),
            overallScore = 96,
            matchScore = 97,
            priceScore = 92,
            valueScore = 94,
            qualityScore = 98,
            riskScore = 8, // Very low
            confidenceScore = 96,
            pros = listOf(
                "کالای پلمپ شرکتی با کد فعال‌سازی آنی",
                "تایید اصالت کالا در سامانه جامع گارانتی",
                "قیمت هماهنگ با کمترین قیمت ترب و ایمالز"
            ),
            source = "ترب (Torob)",
            sourceUrl = "https://torob.com/p/iphone-16-pro-max",
            isDemo = true,
            isBestChoice = true,
            isLowestRisk = true
        ),

        // Samsung Galaxy S25 Ultra
        ProductItem(
            id = "samsung_s25_ultra",
            title = "سامسونگ گلکسی S25 اولترا ۵۱۲ گیگابایت تیتانیوم مشکی",
            category = "کالای دیجیتال و موبایل",
            brand = "سامسونگ",
            model = "Galaxy S25 Ultra",
            year = 2025,
            price = 98000000L,
            formattedPrice = "۹۸ میلیون تومان",
            location = "تبریز، ارک",
            seller = "سامسونگ سنتر سهند",
            sellerType = "نمایندگی رسمی",
            imageUrl = "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=800&auto=format&fit=crop",
            specs = mapOf(
                "پردازنده" to "Snapdragon 8 Elite",
                "حافظه و رم" to "۵۱۲ گیگابایت با ۱۶ گیگابایت رم",
                "دوربین" to "۲۰۰ مگاپیکسل هوش مصنوعی Galaxy AI",
                "گارانتی" to "۱۸ ماه تعویض طلایی هماهنگ"
            ),
            overallScore = 94,
            matchScore = 93,
            priceScore = 95,
            valueScore = 96,
            qualityScore = 95,
            riskScore = 11,
            confidenceScore = 95,
            pros = listOf("گارانتی معتبر شرکتی", "بهترین قیمت موجود در شمال‌غرب"),
            source = "دیجی‌کالا (Digikala)",
            sourceUrl = "https://digikala.com/product/s25-ultra",
            isDemo = true,
            isBestPrice = true
        ),

        // Nike ZoomX Vaporfly 3 Shoes
        ProductItem(
            id = "nike_zoomx_vaporfly3",
            title = "کفش دویدن تخصصی نایک زوم ایکس وپرفلای ۳ اورجینال",
            category = "کفش و پوشاک",
            brand = "نایک",
            model = "ZoomX Vaporfly 3",
            year = 2024,
            price = 19500000L,
            formattedPrice = "۱۹.۵ میلیون تومان",
            location = "تبریز، لاله پارک",
            seller = "فروشگاه رانرز لایف",
            sellerType = "فروشگاه",
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&auto=format&fit=crop",
            specs = mapOf(
                "سایز" to "۴۲ و ۴۳ موجود",
                "فناوری کفی" to "فوم فوق سبک ZoomX با پلیت فیبر کربن Flyplate",
                "کاربری" to "ماراتن و دویدن سرعتی مسابقه‌ای",
                "اصالت" to "۱۰۰٪ اورجینال دارای بارکد و آرتیکل نامبر رسمی جعبه"
            ),
            overallScore = 93,
            matchScore = 96,
            priceScore = 90,
            valueScore = 92,
            qualityScore = 96,
            riskScore = 15,
            confidenceScore = 93,
            pros = listOf("تطابق کامل بارکد بین‌المللی نایک", "امکان پرو و بررسی قبل از خرید حضوری"),
            source = "باسلام (Basalam)",
            sourceUrl = "https://basalam.com/p/nike-zoomx-vaporfly",
            isDemo = true,
            isBestChoice = true
        ),

        // Garmin Watch
        ProductItem(
            id = "garmin_fenix_7_pro",
            title = "ساعت ورزشی هوشمند گارمین فنیکس ۷ پرو سولار تیتانیوم",
            category = "ساعت و اکسسوری",
            brand = "گارمین",
            model = "Fenix 7 Pro Sapphire Solar",
            year = 2024,
            price = 48500000L,
            formattedPrice = "۴۸.۵ میلیون تومان",
            location = "تهران، میرداماد",
            seller = "گارمین استور ایران",
            sellerType = "نمایندگی رسمی",
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop",
            specs = mapOf(
                "شارژدهی" to "تا ۳۷ روز با شارژ نوری خورشیدی",
                "بدنه" to "تیتانیوم گرید هوافضا با شیشه یاقوت سافایر",
                "حسگرها" to "GPS چندبانده، سنسور ضربان نسل ۵، نقشه‌های توپوگرافی آفلاین"
            ),
            overallScore = 95,
            matchScore = 94,
            priceScore = 91,
            valueScore = 95,
            qualityScore = 97,
            riskScore = 9,
            confidenceScore = 97,
            pros = listOf("نمایندگی با ضمانت اصالت فیزیکی مادام‌العمر", "گارانتی ۲ ساله تعویض بدون قید و شرط"),
            source = "ایمالز (Emalls)",
            sourceUrl = "https://emalls.ir/p/garmin-fenix-7-pro",
            isDemo = true,
            isBestChoice = true
        )
    )

    override suspend fun search(query: String, filters: ExtractedFilters?): List<ProductItem> {
        val q = query.trim().lowercase()
        return allCatalog.filter { item ->
            val matchText = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.brand.lowercase().contains(q) ||
                item.model.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.location.lowercase().contains(q) ||
                (q.contains("کرولا") && item.model.contains("کرولا")) ||
                (q.contains("تویوتا") && item.brand.contains("تویوتا")) ||
                (q.contains("کفش") && item.category.contains("کفش")) ||
                (q.contains("آیفون") && item.model.contains("iPhone")) ||
                (q.contains("سامسونگ") && item.brand.contains("سامسونگ")) ||
                (q.contains("ماشین") || q.contains("خودرو")) && item.category.contains("خودرو")

            val matchBrand = filters?.brand.isNullOrEmpty() || item.brand.contains(filters?.brand ?: "", ignoreCase = true)
            val matchCity = filters?.location.isNullOrEmpty() || item.location.contains(filters?.location ?: "", ignoreCase = true)
            val matchPrice = filters?.priceMax == null || item.price <= (filters.priceMax ?: Long.MAX_VALUE)

            matchText && matchBrand && matchCity && matchPrice
        }.ifEmpty {
            // If empty, return all catalog so user always has relevant smart results
            allCatalog
        }
    }

    override suspend fun searchByImage(descriptionOrTag: String): List<ProductItem> {
        val tag = descriptionOrTag.lowercase()
        return allCatalog.filter {
            it.title.lowercase().contains(tag) ||
            it.category.lowercase().contains(tag) ||
            it.brand.lowercase().contains(tag)
        }.ifEmpty { allCatalog.take(4) }
    }

    override suspend fun searchByVideo(videoTagOrUrl: String): List<ProductItem> {
        return allCatalog.take(4)
    }

    override suspend fun searchByFile(documentTitle: String): List<ProductItem> {
        return allCatalog.filter { it.category.contains("خودرو") }
    }
}

class PidayabRepository(
    private val searchProvider: SearchProvider = MarketplaceDemoSearchProvider()
) {
    private val bookmarkedIds = mutableSetOf<String>()
    private val savedSearchesList = mutableListOf(
        "کرولا کراس هیبرید ۲۰۲۵ زیر ۷ میلیارد",
        "آیفون ۱۶ پرومکس ۲۵۶ پارت ZAA",
        "کفش رانینگ نایک سایز ۴۲",
        "سانتافه ۲۰۲۴ تبریز"
    )
    private val recentSearchesList = mutableListOf(
        "تویوتا کرولا کراس ۲۰۲۵ تبریز",
        "آیفون ۱۶ پرومکس",
        "کفش نایک وپرفلای",
        "سامسونگ S25 اولترا"
    )
    private val priceAlertsList = mutableListOf(
        PriceAlert(
            id = "alert_1",
            title = "کرولا کراس هیبرید ۲۰۲۵ (زیر ۶.۷ میلیارد)",
            targetPrice = 6700000000L,
            formattedTargetPrice = "۶.۷۰ میلیارد تومان",
            currentMarketPrice = "۶.۸۵ میلیارد تومان",
            category = "خودرو",
            isActive = true,
            createdDate = "۲ روز پیش"
        ),
        PriceAlert(
            id = "alert_2",
            title = "آیفون ۱۶ پرومکس ۲۵۶ گیگ (زیر ۱۰۵ میلیون)",
            targetPrice = 105000000L,
            formattedTargetPrice = "۱۰۵ میلیون تومان",
            currentMarketPrice = "۱۰۸.۵ میلیون تومان",
            category = "کالای دیجیتال",
            isActive = true,
            createdDate = "هفته گذشته"
        )
    )

    var userPreferences = UserPreferences()

    suspend fun performSearch(query: String, filters: ExtractedFilters? = null): List<ProductItem> {
        if (query.isNotBlank() && !recentSearchesList.contains(query.trim())) {
            recentSearchesList.add(0, query.trim())
        }
        val items = searchProvider.search(query, filters)
        return items.map { item ->
            item.copy(isBookmarked = bookmarkedIds.contains(item.id))
        }
    }

    suspend fun performVisualSearch(tag: String): List<ProductItem> {
        val items = searchProvider.searchByImage(tag)
        return items.map { item -> item.copy(isBookmarked = bookmarkedIds.contains(item.id)) }
    }

    suspend fun performVideoSearch(videoTag: String): List<ProductItem> {
        val items = searchProvider.searchByVideo(videoTag)
        return items.map { item -> item.copy(isBookmarked = bookmarkedIds.contains(item.id)) }
    }

    suspend fun performFileSearch(fileTitle: String): List<ProductItem> {
        val items = searchProvider.searchByFile(fileTitle)
        return items.map { item -> item.copy(isBookmarked = bookmarkedIds.contains(item.id)) }
    }

    fun toggleBookmark(productId: String) {
        if (bookmarkedIds.contains(productId)) {
            bookmarkedIds.remove(productId)
        } else {
            bookmarkedIds.add(productId)
        }
    }

    fun isBookmarked(productId: String): Boolean = bookmarkedIds.contains(productId)

    fun getBookmarkedItems(allItems: List<ProductItem>): List<ProductItem> {
        return allItems.filter { bookmarkedIds.contains(it.id) }
    }

    fun getSavedSearches(): List<String> = savedSearchesList.toList()
    fun addSavedSearch(search: String) {
        if (search.isNotBlank() && !savedSearchesList.contains(search.trim())) {
            savedSearchesList.add(0, search.trim())
        }
    }

    fun getRecentSearches(): List<String> = recentSearchesList.toList()

    fun getPriceAlerts(): List<PriceAlert> = priceAlertsList.toList()
    fun addPriceAlert(alert: PriceAlert) {
        priceAlertsList.add(0, alert)
    }
    fun togglePriceAlert(id: String) {
        val index = priceAlertsList.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = priceAlertsList[index]
            priceAlertsList[index] = item.copy(isActive = !item.isActive)
        }
    }
}
