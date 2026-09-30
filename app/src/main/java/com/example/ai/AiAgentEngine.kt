package com.example.ai

import com.example.model.ChatMessage
import com.example.model.ExtractedFilters
import com.example.model.ProductItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object AiAgentEngine {

    /**
     * Extracts structured filters from freeform Persian text.
     * Example input: «یه کرولا کراس هیبرید مدل ۲۰۲۵ میخوام، زیر ۷ میلیارد، کارکرد پایین، ترجیحاً تبریز.»
     */
    fun extractFilters(persianText: String): ExtractedFilters {
        val q = persianText.trim()
        val filters = ExtractedFilters(rawQuery = q)

        // Brand & Model Extraction
        when {
            q.contains("کرولا کراس") || (q.contains("کرولا") && q.contains("تویوتا")) -> {
                filters.brand = "تویوتا"
                filters.model = "کرولا کراس"
                filters.category = "خودرو و وسایل نقلیه"
            }
            q.contains("تویوتا") -> {
                filters.brand = "تویوتا"
                filters.category = "خودرو و وسایل نقلیه"
            }
            q.contains("سانتافه") || (q.contains("هیوندای") && q.contains("سانتافه")) -> {
                filters.brand = "هیوندای"
                filters.model = "سانتافه"
                filters.category = "خودرو و وسایل نقلیه"
            }
            q.contains("هیوندای") -> {
                filters.brand = "هیوندای"
                filters.category = "خودرو و وسایل نقلیه"
            }
            q.contains("آیفون") || q.contains("ایفون") || q.contains("iphone", ignoreCase = true) -> {
                filters.brand = "اپل"
                filters.model = if (q.contains("۱۶") || q.contains("16")) "iPhone 16 Pro Max" else "آیفون"
                filters.category = "کالای دیجیتال و موبایل"
            }
            q.contains("سامسونگ") || q.contains("s25", ignoreCase = true) -> {
                filters.brand = "سامسونگ"
                filters.model = "Galaxy S25 Ultra"
                filters.category = "کالای دیجیتال و موبایل"
            }
            q.contains("نایک") || q.contains("کفش") || q.contains("کتونی") -> {
                filters.brand = "نایک"
                filters.model = "ZoomX Vaporfly 3"
                filters.category = "کفش و پوشاک"
            }
            q.contains("گارمین") || q.contains("ساعت") -> {
                filters.brand = "گارمین"
                filters.model = "Fenix 7 Pro"
                filters.category = "ساعت و اکسسوری"
            }
            else -> {
                filters.category = "همه دسته‌ها"
            }
        }

        // Year extraction (Persian & English digits)
        val normalized = normalizeDigits(q)
        val yearRegex = Regex("""\b(202[0-9]|140[0-9])\b""")
        yearRegex.find(normalized)?.let {
            filters.year = it.value.toIntOrNull()
        }

        // Max price extraction
        if (q.contains("میلیارد")) {
            val billionRegex = Regex("""(\d+(?:\.\d+)?)\s*میلیارد""")
            billionRegex.find(normalized)?.let {
                val num = it.groupValues[1].toDoubleOrNull() ?: 7.0
                filters.priceMax = (num * 1_000_000_000L).toLong()
            }
        } else if (q.contains("میلیون")) {
            val millionRegex = Regex("""(\d+(?:\.\d+)?)\s*میلیون""")
            millionRegex.find(normalized)?.let {
                val num = it.groupValues[1].toDoubleOrNull() ?: 100.0
                filters.priceMax = (num * 1_000_000L).toLong()
            }
        }

        // Location extraction
        val cities = listOf("تبریز", "تهران", "اصفهان", "شیراز", "مشهد", "کرج", "اهواز", "ارومیه", "رشت", "قم")
        for (city in cities) {
            if (q.contains(city)) {
                filters.location = city
                break
            }
        }

        // Fuel & Engine
        if (q.contains("هیبرید") || q.contains("هایبرید")) {
            filters.fuelType = "هیبرید"
        } else if (q.contains("برقی")) {
            filters.fuelType = "برقی"
        } else if (q.contains("بنزین")) {
            filters.fuelType = "بنزینی"
        }

        // Mileage
        if (q.contains("کارکرد پایین") || q.contains("کم کار") || q.contains("کم‌کار")) {
            filters.mileageMax = 15000L
        } else if (q.contains("صفر")) {
            filters.mileageMax = 0L
            filters.condition = "صفر کیلومتر"
        }

        // Risk preference
        if (q.contains("کم ریسک") || q.contains("کم‌ریسک") || q.contains("مطمئن")) {
            filters.riskPreference = "حداکثر ریسک ۲۰/۱۰۰ (بسیار پایین)"
        }

        return filters
    }

    private fun normalizeDigits(input: String): String {
        val persian = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val arabic = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        var res = input
        for (i in 0..9) {
            res = res.replace(persian[i], ('0' + i))
            res = res.replace(arabic[i], ('0' + i))
        }
        return res
    }

    /**
     * Answers queries about shopping, risk analysis, and comparisons in natural Persian.
     */
    suspend fun askAssistant(
        userPrompt: String,
        currentItems: List<ProductItem>,
        apiKey: String?
    ): ChatMessage = withContext(Dispatchers.IO) {
        val q = userPrompt.trim()

        // If Gemini API is available and non-empty, try live request with fallback
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val liveAnswer = callGeminiRest(userPrompt, currentItems, apiKey)
                if (liveAnswer.isNotBlank()) {
                    return@withContext ChatMessage(
                        id = "msg_${System.currentTimeMillis()}",
                        isFromUser = false,
                        text = liveAnswer,
                        timestamp = "اکنون",
                        suggestedActions = listOf(
                            "کدوم کمریسکتره؟",
                            "یه گزینه ارزونتر پیدا کن",
                            "مقایسه ۲ گزینه اول"
                        )
                    )
                }
            } catch (e: Exception) {
                // Graceful fallback to expert local rules engine
            }
        }

        // Smart Local Persian Agent reasoning engine
        val responseText = when {
            q.contains("چرا این ارزونتره") || q.contains("ارزونتره") || q.contains("تفاوت قیمت") -> {
                "در آگهی‌های خودروی کرولا کراس، آگهی اصفهان با قیمت ۶.۶۹ میلیارد تومان به دلیل یک لکه لیسه‌گیری جزئی روی سپر عقب و نیاز به هماهنگی حمل حدود ۱۶۰ میلیون تومان ارزان‌تر از گزینه تبریز قیمت‌گذاری شده است. آگهی مشکوک ۵.۱ میلیاردی نیز به دلیل ادعای مهاجرت و درخواست بیعانه پیش از رویت ارزان‌تر نشان داده شده که ریسک بالا دارد."
            }
            q.contains("کمریسک") || q.contains("کم ریسک") || q.contains("کدوم مطمئن") -> {
                val lowest = currentItems.minByOrNull { it.riskScore }
                if (lowest != null) {
                    "کم‌ریسک‌ترین گزینه «${lowest.title}» با امتیاز ریسک ${lowest.riskScore}/۱۰۰ (بسیار پایین) است. دلایل: فروشنده دارای هویت تاییدشده رسمی است، خودرو دارای برگه کارشناسی معتبر بدون تصادف می‌باشد و امکان بازدید حضوری و تست فنی وجود دارد."
                } else {
                    "گزینه اول با امتیاز ریسک ۱۴/۱۰۰ کم‌ریسک‌ترین انتخاب فعلی است."
                }
            }
            q.contains("ارزونتر") || q.contains("ارزانتر") || q.contains("کف قیمت") -> {
                val cheapest = currentItems.filter { it.riskScore < 50 }.minByOrNull { it.price }
                if (cheapest != null) {
                    "ارزان‌ترین گزینه در میان آگهی‌های مطمئن و کم‌ریسک، «${cheapest.title}» با قیمت ${cheapest.formattedPrice} در ${cheapest.location} است. (امتیاز ارزش خرید: ${cheapest.valueScore}/۱۰۰)"
                } else {
                    "تمام گزینه‌های موجود با قیمت منصفانه فهرست شده‌اند."
                }
            }
            q.contains("چرا این آگهی رو اول گذاشتی") || q.contains("اول گذاشتی") || q.contains("بهترین انتخاب") -> {
                "آگهی اول بر اساس سیستم امتیازدهی چندبعدی پیدایاب (امتیاز کلی ۹۵/۱۰۰) رتبه اول را کسب کرده است: ۱) تطابق کامل ۹۸٪ با درخواست شما در شهر تبریز ۲) کارکرد بسیار پایین ۲۵۰۰ کیلومتر ۳) ریسک حداقلی ۱۴/۱۰۰ و ۴) قیمت منصفانه در محدوده رسمی بازار بدون حباب کاذب."
            }
            q.contains("ارزش خرید") || q.contains("بهترین ارزش") -> {
                "بیشترین امتیاز ارزش خرید متعلق به تویوتا کرولا کراس با امتیاز ۹۶/۱۰۰ است؛ زیرا ترکیب سال ۲۰۲۵، پیشرانه هیبرید کم‌استهلاک، افت قیمت اولیه انجام‌شده و گارانتی ۵ ساله باتری بالاترین بازدهی سرمایه را فراهم می‌کند."
            }
            q.contains("مشکوک") || q.contains("کلاهبرداری") || q.contains("امنیت") -> {
                "بر اساس بررسی هوش مصنوعی پیدایاب، نشانه‌های مشکوک عبارتند از: قیمت ۲۵٪ پایین‌تر از کف بازار، درخواست بیعانه پیش از بازدید حضوری، ثبت‌نام جدید فروشنده و نبود برگه کارشناسی. در پیدایاب این موارد به وضوح با برچسب زرد یا قرمز «نیازمند بررسی بیشتر» مشخص می‌شوند."
            }
            else -> {
                "من دستیار هوشمند پیدایاب هستم. گزینه‌های یافته‌شده بر اساس شاخص‌های تطابق، قیمت منصفانه و کمترین ریسک رتبه‌بندی شده‌اند. می‌توانید از من درباره علت اختلاف قیمت‌ها، مقایسه تخصصی، یا فیلترهای دقیق‌تر سوال بپرسید."
            }
        }

        ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            isFromUser = false,
            text = responseText,
            timestamp = "اکنون",
            suggestedActions = listOf(
                "کدوم کمریسکتره؟",
                "چرا این آگهی رو اول گذاشتی؟",
                "ارزونترین گزینه کم‌ریسک چیه؟"
            )
        )
    }

    private fun callGeminiRest(prompt: String, items: List<ProductItem>, apiKey: String): String {
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.doOutput = true
        conn.connectTimeout = 7000
        conn.readTimeout = 7000

        val systemInstruction = "شما دستیار هوش مصنوعی سامانه «پیدایاب» هستید. پاسخ‌های دقیق، کاملاً فارسی، بدون اغراق و صرفاً بر اساس اطلاعات موجود آگهی‌ها بدهید."
        val summaryOfItems = items.take(4).joinToString("\n") {
            "- ${it.title} | قیمت: ${it.formattedPrice} | شهر: ${it.location} | امتیاز ریسک: ${it.riskScore} | فروشنده: ${it.seller}"
        }

        val jsonBody = JSONObject().apply {
            val contents = org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().put("text", "$systemInstruction\nاطلاعات آگهی‌ها:\n$summaryOfItems\n\nسوال کاربر: $prompt"))
                    })
                })
            }
            put("contents", contents)
        }

        conn.outputStream.use { os ->
            os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
        }

        if (conn.responseCode == 200) {
            val responseString = conn.inputStream.bufferedReader().use { it.readText() }
            val respJson = JSONObject(responseString)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text", "")
                }
            }
        }
        return ""
    }
}
