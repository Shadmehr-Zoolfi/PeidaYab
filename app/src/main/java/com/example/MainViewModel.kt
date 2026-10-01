package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAgentEngine
import com.example.data.PidayabRepository
import com.example.data.local.SearchHistoryEntity
import com.example.data.local.SearchHistoryRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: PidayabRepository = PidayabRepository(),
    private var historyRepository: SearchHistoryRepository? = null
) : ViewModel() {

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _selectedDetailItem = MutableStateFlow<ProductItem?>(null)
    val selectedDetailItem: StateFlow<ProductItem?> = _selectedDetailItem.asStateFlow()

    private val _searchQuery = MutableStateFlow("کرولا کراس ۲۰۲۵")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductItem>>(emptyList())
    val searchResults: StateFlow<List<ProductItem>> = _searchResults.asStateFlow()

    private val _featuredItems = MutableStateFlow<List<ProductItem>>(emptyList())
    val featuredItems: StateFlow<List<ProductItem>> = _featuredItems.asStateFlow()

    private val _extractedFilters = MutableStateFlow<ExtractedFilters?>(null)
    val extractedFilters: StateFlow<ExtractedFilters?> = _extractedFilters.asStateFlow()

    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()

    private val _showAgentProgress = MutableStateFlow(false)
    val showAgentProgress: StateFlow<Boolean> = _showAgentProgress.asStateFlow()

    private val _showVoiceDialog = MutableStateFlow(false)
    val showVoiceDialog: StateFlow<Boolean> = _showVoiceDialog.asStateFlow()

    private val _showCameraScreen = MutableStateFlow(false)
    val showCameraScreen: StateFlow<Boolean> = _showCameraScreen.asStateFlow()

    private val _roomSearchHistory = MutableStateFlow<List<SearchHistoryEntity>>(emptyList())
    val roomSearchHistory: StateFlow<List<SearchHistoryEntity>> = _roomSearchHistory.asStateFlow()

    private val _agentSteps = MutableStateFlow<List<SearchAgentStep>>(emptyList())
    val agentSteps: StateFlow<List<SearchAgentStep>> = _agentSteps.asStateFlow()

    private val _agentFoundCount = MutableStateFlow<Int?>(null)
    val agentFoundCount: StateFlow<Int?> = _agentFoundCount.asStateFlow()

    private val _comparisonItems = MutableStateFlow<List<ProductItem>>(emptyList())
    val comparisonItems: StateFlow<List<ProductItem>> = _comparisonItems.asStateFlow()

    private val _savedSearches = MutableStateFlow<List<String>>(emptyList())
    val savedSearches: StateFlow<List<String>> = _savedSearches.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _priceAlerts = MutableStateFlow<List<PriceAlert>>(emptyList())
    val priceAlerts: StateFlow<List<PriceAlert>> = _priceAlerts.asStateFlow()

    private val _userPreferences = MutableStateFlow(repository.userPreferences)
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAssistantThinking = MutableStateFlow(false)
    val isAssistantThinking: StateFlow<Boolean> = _isAssistantThinking.asStateFlow()

    private val _shareText = MutableStateFlow<String?>(null)
    val shareText: StateFlow<String?> = _shareText.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isRealWebResults = MutableStateFlow(false)
    val isRealWebResults: StateFlow<Boolean> = _isRealWebResults.asStateFlow()

    private val _forceDemoMode = MutableStateFlow(!repository.isRealWebActive())
    val forceDemoMode: StateFlow<Boolean> = _forceDemoMode.asStateFlow()

    private val _providersState = MutableStateFlow<List<ProviderState>>(repository.getProvidersState())
    val providersState: StateFlow<List<ProviderState>> = _providersState.asStateFlow()

    private val _priceAnalysis = MutableStateFlow<PriceAnalysisResult>(PriceAnalysisResult(hasSufficientData = false))
    val priceAnalysis: StateFlow<PriceAnalysisResult> = _priceAnalysis.asStateFlow()

    private val _lastCheckedTime = MutableStateFlow<String?>("امروز")
    val lastCheckedTime: StateFlow<String?> = _lastCheckedTime.asStateFlow()

    private val _searchStatusBanner = MutableStateFlow<String?>(null)
    val searchStatusBanner: StateFlow<String?> = _searchStatusBanner.asStateFlow()

    init {
        loadInitialData()
        observeHistory()
    }

    fun initHistory(repo: SearchHistoryRepository) {
        historyRepository = repo
        observeHistory()
    }

    private fun observeHistory() {
        val repo = historyRepository ?: return
        viewModelScope.launch {
            repo.allHistory.collect { historyList ->
                _roomSearchHistory.value = historyList
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val initialSearch = repository.performSearchWithState("کرولا کراس ۲۰۲۵", forceDemo = _forceDemoMode.value)
            val initialList = if (initialSearch is SearchResultState.Success) initialSearch.items else emptyList()
            _featuredItems.value = initialList
            _searchResults.value = initialList
            _priceAnalysis.value = repository.calculatePriceAnalysis(initialList)
            if (initialSearch is SearchResultState.Success) {
                _isRealWebResults.value = initialSearch.isFromRealWeb
                _lastCheckedTime.value = initialSearch.cachedTime
                _searchStatusBanner.value = initialSearch.message
            }
            _savedSearches.value = repository.getSavedSearches()
            _recentSearches.value = repository.getRecentSearches()
            _priceAlerts.value = repository.getPriceAlerts()

            // Pre-select first two items for comparison showcase
            if (initialList.size >= 2) {
                _comparisonItems.value = listOf(initialList[0], initialList[1])
            }

            // Initial AI welcome message
            _chatMessages.value = listOf(
                ChatMessage(
                    id = "welcome_msg",
                    isFromUser = false,
                    text = "سلام! من دستیار هوشمند «پیدایاب» هستم. می‌توانید هر سوالی درباره اختلاف قیمت آگهی‌ها، دلایل رتبه‌بندی، یا تحلیل ریسک خریدهای مدنظرتان دارید بپرسید.",
                    timestamp = "الان",
                    suggestedActions = listOf(
                        "چرا این ارزونتره؟",
                        "کدوم کمریسکتره؟",
                        "ارزونترین گزینه کم‌ریسک رو پیدا کن"
                    )
                )
            )
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedDetailItem.value = null
        _currentTab.value = tabIndex
    }

    fun openDetail(item: ProductItem) {
        _selectedDetailItem.value = item
    }

    fun closeDetail() {
        _selectedDetailItem.value = null
    }

    fun toggleForceDemo(enabled: Boolean) {
        _forceDemoMode.value = enabled
        startSearchWithAgent(_searchQuery.value, _extractedFilters.value)
    }

    fun openRealSourceUrl(context: android.content.Context, url: String) {
        if (!com.example.data.engine.UrlValidator.isValidWebUrl(url)) {
            _snackbarMessage.value = "آدرس اینترنتی این آگهی معتبر نیست."
            return
        }
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            _snackbarMessage.value = "امکان باز کردن پیوند در مرورگر وجود ندارد."
        }
    }

    /**
     * Autonomous search agent execution with 7-step animated progress (Section 7).
     */
    fun startSearchWithAgent(query: String, explicitFilters: ExtractedFilters? = null) {
        val extracted = explicitFilters ?: AiAgentEngine.extractFilters(query)
        _extractedFilters.value = extracted
        _searchQuery.value = query

        viewModelScope.launch {
            _showAgentProgress.value = true
            _agentFoundCount.value = null

            val initialSteps = listOf(
                SearchAgentStep(1, "در حال فهمیدن درخواست شما", isDone = false, isActive = true),
                SearchAgentStep(2, "در حال جستجوی منابع مجاز", isDone = false, isActive = false),
                SearchAgentStep(3, "در حال پیدا کردن آگهی‌ها", isDone = false, isActive = false),
                SearchAgentStep(4, "در حال مقایسه قیمت‌ها", isDone = false, isActive = false),
                SearchAgentStep(5, "در حال بررسی اطلاعات و اصالت", isDone = false, isActive = false),
                SearchAgentStep(6, "در حال بررسی ریسک و علائم مشکوک", isDone = false, isActive = false),
                SearchAgentStep(7, "در حال رتبه‌بندی نتایج بر اساس نیاز شما", isDone = false, isActive = false)
            )
            _agentSteps.value = initialSteps

            for (i in 0 until initialSteps.size) {
                delay(300)
                _agentSteps.value = _agentSteps.value.mapIndexed { index, step ->
                    when {
                        index < i -> step.copy(isDone = true, isActive = false)
                        index == i -> step.copy(isDone = false, isActive = true)
                        else -> step.copy(isDone = false, isActive = false)
                    }
                }
            }

            val searchState = repository.performSearchWithState(query, extracted, forceDemo = _forceDemoMode.value)
            when (searchState) {
                is SearchResultState.Success -> {
                    _searchResults.value = searchState.items
                    _isRealWebResults.value = searchState.isFromRealWeb
                    _lastCheckedTime.value = searchState.cachedTime
                    _searchStatusBanner.value = searchState.message
                    _priceAnalysis.value = repository.calculatePriceAnalysis(searchState.items)
                    _agentFoundCount.value = searchState.items.size
                }
                is SearchResultState.Empty -> {
                    _searchResults.value = emptyList()
                    _isRealWebResults.value = false
                    _searchStatusBanner.value = searchState.message
                    _priceAnalysis.value = repository.calculatePriceAnalysis(emptyList())
                    _agentFoundCount.value = 0
                }
                is SearchResultState.Error -> {
                    _searchStatusBanner.value = searchState.message
                    _snackbarMessage.value = searchState.message
                    if (searchState.canFallbackToDemo) {
                        val fallback = repository.performSearch(query, extracted)
                        _searchResults.value = fallback
                        _isRealWebResults.value = false
                        _priceAnalysis.value = repository.calculatePriceAnalysis(fallback)
                        _agentFoundCount.value = fallback.size
                    } else {
                        _searchResults.value = emptyList()
                        _agentFoundCount.value = 0
                    }
                }
                is SearchResultState.Loading -> {}
            }

            _agentSteps.value = _agentSteps.value.map { it.copy(isDone = true, isActive = false) }
            delay(350)

            _showAgentProgress.value = false
            _recentSearches.value = repository.getRecentSearches()

            // Save to local Room database schema
            val count = _agentFoundCount.value ?: _searchResults.value.size
            val isVehicle = extracted.category == "خودرو" || query.contains("کرولا") || query.contains("خودرو") || query.contains("ماشین") || query.contains("پژو") || query.contains("تویوتا")
            val type = if (isVehicle) "VEHICLE" else "PRODUCT"
            historyRepository?.recordSearch(query, type, count, extracted)

            _currentTab.value = 1 // Switch to Search results tab
        }
    }

    fun openVoiceSearch() {
        _showVoiceDialog.value = true
    }

    fun closeVoiceSearch() {
        _showVoiceDialog.value = false
    }

    fun openCameraSearch() {
        _showCameraScreen.value = true
    }

    fun closeCameraSearch() {
        _showCameraScreen.value = false
    }

    fun performVoiceSearch(transcribedText: String) {
        _showVoiceDialog.value = false
        val clean = transcribedText.trim()
        if (clean.isBlank()) return
        val extracted = AiAgentEngine.extractFilters(clean)
        val isVehicle = extracted.category == "خودرو" || clean.contains("کرولا") || clean.contains("خودرو") || clean.contains("ماشین")
        val searchType = if (isVehicle) "VEHICLE" else "VOICE"

        startSearchWithAgent(clean, extracted)
        viewModelScope.launch {
            historyRepository?.recordSearch(clean, searchType, _searchResults.value.size, extracted)
            _snackbarMessage.value = "جستجوی صوتی انجام شد: «$clean»"
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyRepository?.deleteHistoryItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository?.clearAllHistory()
            _snackbarMessage.value = "تاریخچه جستجوهای محلی پاک شد"
        }
    }

    fun onPromptSubmittedFromHome(prompt: String) {
        val extracted = AiAgentEngine.extractFilters(prompt)
        _extractedFilters.value = extracted
        _searchQuery.value = prompt
        _showFilterDialog.value = true
    }

    fun confirmExtractedFiltersAndSearch(filters: ExtractedFilters) {
        _showFilterDialog.value = false
        startSearchWithAgent(filters.rawQuery.ifBlank { "${filters.brand ?: ""} ${filters.model ?: ""}" }, filters)
    }

    fun dismissFilterDialog() {
        _showFilterDialog.value = false
    }

    fun performVisualSearch(tag: String) {
        viewModelScope.launch {
            val results = repository.performVisualSearch(tag)
            _searchResults.value = results
            _searchQuery.value = "جستجوی بصری: $tag"
            _currentTab.value = 1
            historyRepository?.recordSearch("جستجوی بصری: $tag", "CAMERA", results.size)
            _snackbarMessage.value = "تصویر تحلیل شد و ${results.size} مورد منطبق در کاتالوگ آزمایشی یافت گردید."
        }
    }

    fun performVideoSearch(videoTag: String) {
        viewModelScope.launch {
            val results = repository.performVideoSearch(videoTag)
            _searchResults.value = results
            _searchQuery.value = "تحلیل ویدیویی: $videoTag"
            _currentTab.value = 1
            _snackbarMessage.value = "فریم‌های ویدیو بررسی شد و کالای منطبق در کاتالوگ آزمایشی شناسایی شد."
        }
    }

    fun performFileSearch(fileTitle: String) {
        viewModelScope.launch {
            val results = repository.performFileSearch(fileTitle)
            _searchResults.value = results
            _searchQuery.value = "تحلیل کاتالوگ: $fileTitle"
            _currentTab.value = 1
            _snackbarMessage.value = "داده‌های فایل استخراج و با آگهی‌های کاتالوگ آزمایشی تطبیق داده شد."
        }
    }

    fun toggleBookmark(productId: String) {
        repository.toggleBookmark(productId)
        // Refresh items with updated bookmark state
        _searchResults.value = _searchResults.value.map {
            if (it.id == productId) it.copy(isBookmarked = repository.isBookmarked(productId)) else it
        }
        _featuredItems.value = _featuredItems.value.map {
            if (it.id == productId) it.copy(isBookmarked = repository.isBookmarked(productId)) else it
        }
        _selectedDetailItem.value?.let {
            if (it.id == productId) {
                _selectedDetailItem.value = it.copy(isBookmarked = repository.isBookmarked(productId))
            }
        }
        val isNowBookmarked = repository.isBookmarked(productId)
        _snackbarMessage.value = if (isNowBookmarked) "آگهی به نشان‌شده‌ها افزوده شد" else "آگهی از نشان‌شده‌ها حذف شد"
    }

    fun addToComparison(item: ProductItem) {
        val current = _comparisonItems.value.toMutableList()
        if (current.none { it.id == item.id }) {
            if (current.size >= 4) {
                current.removeAt(0)
            }
            current.add(item)
            _comparisonItems.value = current
            _snackbarMessage.value = "«${item.title}» به میز مقایسه افزوده شد"
        } else {
            _snackbarMessage.value = "این مورد قبلاً در مقایسه وجود دارد"
        }
        _currentTab.value = 2 // Switch to Comparison tab
    }

    fun removeFromComparison(item: ProductItem) {
        _comparisonItems.value = _comparisonItems.value.filter { it.id != item.id }
    }

    fun addPriceAlert(alert: PriceAlert) {
        repository.addPriceAlert(alert)
        _priceAlerts.value = repository.getPriceAlerts()
        _snackbarMessage.value = "هشدار قیمت با موفقیت تنظیم شد"
    }

    fun togglePriceAlert(alertId: String) {
        repository.togglePriceAlert(alertId)
        _priceAlerts.value = repository.getPriceAlerts()
    }

    fun sendAssistantMessage(text: String) {
        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            isFromUser = true,
            text = text,
            timestamp = "الان"
        )
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAssistantThinking.value = true
            delay(500)
            val apiKey = try {
                com.example.BuildConfig.GEMINI_API_KEY
            } catch (e: Throwable) {
                null
            }
            val reply = AiAgentEngine.askAssistant(text, _searchResults.value, apiKey)
            _isAssistantThinking.value = false
            _chatMessages.value = _chatMessages.value + reply
        }
    }

    fun generateShareSummary(item: ProductItem) {
        val summary = """
            «پیدایاب - خلاصه هوشمند آگهی»
            نام کالا: ${item.title}
            قیمت: ${item.formattedPrice}
            امتیاز کلی پیدایاب: ${item.overallScore}/۱۰۰
            امتیاز ریسک: ${item.riskScore}/۱۰۰ (${item.riskLabel})
            تطابق با بازار: ${item.matchScore}٪
            منبع: ${item.source} (${item.location})
            هشدارها: ${item.warnings.joinToString("، ").ifEmpty { "موردی یافت نشد" }}
        """.trimIndent()
        _shareText.value = summary
        _snackbarMessage.value = "خلاصه مشخصات آگهی برای اشتراک‌گذاری کپی شد"
    }

    fun dismissShareDialog() {
        _shareText.value = null
    }

    fun updateUserPreferences(pref: UserPreferences) {
        _userPreferences.value = pref
        repository.userPreferences = pref
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
