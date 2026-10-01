package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.SearchHistoryRepository
import com.example.model.PriceAlert
import com.example.ui.camera.CameraSearchScreen
import com.example.ui.components.ExtractedFiltersDialog
import com.example.ui.components.SearchAgentProgressDialog
import com.example.ui.screens.*
import com.example.ui.theme.PidayabPrimary
import com.example.ui.theme.PidayabTheme
import com.example.ui.voice.VoiceSearchDialog

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userPrefs by viewModel.userPreferences.collectAsState()

            PidayabTheme(darkTheme = userPrefs.isDarkMode) {
                // Persian Right-To-Left layout direction across the entire app
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    PidayabApp(viewModel = viewModel)
                }
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun PidayabApp(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedDetailItem by viewModel.selectedDetailItem.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val featuredItems by viewModel.featuredItems.collectAsState()
    val extractedFilters by viewModel.extractedFilters.collectAsState()
    val showFilterDialog by viewModel.showFilterDialog.collectAsState()
    val showAgentProgress by viewModel.showAgentProgress.collectAsState()
    val agentSteps by viewModel.agentSteps.collectAsState()
    val agentFoundCount by viewModel.agentFoundCount.collectAsState()
    val comparisonItems by viewModel.comparisonItems.collectAsState()
    val savedSearches by viewModel.savedSearches.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val priceAlerts by viewModel.priceAlerts.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAssistantThinking by viewModel.isAssistantThinking.collectAsState()
    val shareText by viewModel.shareText.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val isRealWebResults by viewModel.isRealWebResults.collectAsState()
    val forceDemoMode by viewModel.forceDemoMode.collectAsState()
    val priceAnalysis by viewModel.priceAnalysis.collectAsState()
    val lastCheckedTime by viewModel.lastCheckedTime.collectAsState()
    val searchStatusBanner by viewModel.searchStatusBanner.collectAsState()
    val providersState by viewModel.providersState.collectAsState()
    val showVoiceDialog by viewModel.showVoiceDialog.collectAsState()
    val showCameraScreen by viewModel.showCameraScreen.collectAsState()
    val roomSearchHistory by viewModel.roomSearchHistory.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        val db = AppDatabase.getInstance(context)
        viewModel.initHistory(SearchHistoryRepository(db.searchHistoryDao()))
    }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Handle system back navigation
    BackHandler(enabled = selectedDetailItem != null || currentTab != 0) {
        if (selectedDetailItem != null) {
            viewModel.closeDetail()
        } else if (currentTab != 0) {
            viewModel.selectTab(0)
        }
    }

    val navItems = listOf(
        BottomNavItem("خانه", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
        BottomNavItem("جستجو", Icons.Filled.Search, Icons.Outlined.Search, "nav_search"),
        BottomNavItem("مقایسه", Icons.Filled.CompareArrows, Icons.Outlined.CompareArrows, "nav_compare"),
        BottomNavItem("ذخیره‌ها", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, "nav_saved"),
        BottomNavItem("دستیار", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_assistant"),
        BottomNavItem("پروفایل", Icons.Filled.Person, Icons.Outlined.PersonOutline, "nav_profile")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (selectedDetailItem == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = currentTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(index) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = PidayabPrimary
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedDetailItem != null) {
                ProductDetailScreen(
                    item = selectedDetailItem!!,
                    onBack = { viewModel.closeDetail() },
                    onCompare = { viewModel.addToComparison(it) },
                    onToggleBookmark = { viewModel.toggleBookmark(it) },
                    onSetPriceAlert = {
                        val alert = PriceAlert(
                            id = "alert_${System.currentTimeMillis()}",
                            title = it.title,
                            targetPrice = (it.price * 0.95).toLong(),
                            formattedTargetPrice = "${(it.price * 0.95 / 1_000_000_000.0).let { "%.2f".format(it) }} میلیارد تومان",
                            currentMarketPrice = it.formattedPrice,
                            category = it.category,
                            isActive = true,
                            createdDate = "امروز"
                        )
                        viewModel.addPriceAlert(alert)
                    },
                    onShare = { viewModel.generateShareSummary(it) },
                    onOpenSourceUrl = { viewModel.openRealSourceUrl(context, it) }
                )
            } else {
                when (currentTab) {
                    0 -> HomeScreen(
                        featuredItems = featuredItems,
                        recentSearches = recentSearches,
                        savedSearches = savedSearches,
                        roomSearchHistory = roomSearchHistory,
                        onSearchSubmit = { viewModel.onPromptSubmittedFromHome(it) },
                        onCategoryClick = { viewModel.startSearchWithAgent(it) },
                        onOpenVoiceSearch = { viewModel.openVoiceSearch() },
                        onOpenCameraSearch = { viewModel.openCameraSearch() },
                        onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                        onClearAllHistory = { viewModel.clearAllHistory() },
                        onVisualSearchClick = {
                            viewModel.selectTab(1)
                            viewModel.performVisualSearch("خودرو تویوتا")
                        },
                        onVideoSearchClick = {
                            viewModel.selectTab(1)
                            viewModel.performVideoSearch("Instagram Reel کفش ورزشی")
                        },
                        onFileSearchClick = {
                            viewModel.selectTab(1)
                            viewModel.performFileSearch("لیست_قیمت_کاتالوگ.pdf")
                        },
                        onViewDetail = { viewModel.openDetail(it) },
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onCompare = { viewModel.addToComparison(it) },
                        onShare = { viewModel.generateShareSummary(it) }
                    )
                    1 -> UniversalSearchScreen(
                        initialQuery = searchQuery,
                        searchResults = searchResults,
                        extractedFilters = extractedFilters,
                        roomSearchHistory = roomSearchHistory,
                        onSearchTriggered = { q, filters -> viewModel.startSearchWithAgent(q, filters) },
                        onOpenVoiceSearch = { viewModel.openVoiceSearch() },
                        onOpenCameraSearch = { viewModel.openCameraSearch() },
                        onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                        onClearAllHistory = { viewModel.clearAllHistory() },
                        onVisualSearch = { viewModel.performVisualSearch(it) },
                        onVideoSearch = { viewModel.performVideoSearch(it) },
                        onFileSearch = { viewModel.performFileSearch(it) },
                        onOpenFilterDialog = {
                            extractedFilters?.let { viewModel.onPromptSubmittedFromHome(it.rawQuery) }
                        },
                        onViewDetail = { viewModel.openDetail(it) },
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onCompare = { viewModel.addToComparison(it) },
                        onShare = { viewModel.generateShareSummary(it) },
                        isRealWebResults = isRealWebResults,
                        forceDemoMode = forceDemoMode,
                        onToggleForceDemo = { viewModel.toggleForceDemo(it) },
                        priceAnalysis = priceAnalysis,
                        lastCheckedTime = lastCheckedTime,
                        searchStatusBanner = searchStatusBanner
                    )
                    2 -> ComparisonScreen(
                        itemsToCompare = comparisonItems,
                        onRemoveItem = { viewModel.removeFromComparison(it) },
                        onBack = { viewModel.selectTab(0) },
                        onViewDetail = { viewModel.openDetail(it) }
                    )
                    3 -> AlertsAndSavedScreen(
                        savedItems = searchResults.filter { it.isBookmarked },
                        savedSearches = savedSearches,
                        priceAlerts = priceAlerts,
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onViewDetail = { viewModel.openDetail(it) },
                        onCompare = { viewModel.addToComparison(it) },
                        onShare = { viewModel.generateShareSummary(it) },
                        onExecuteSearch = { viewModel.startSearchWithAgent(it) },
                        onToggleAlert = { viewModel.togglePriceAlert(it) },
                        onAddPriceAlert = { viewModel.addPriceAlert(it) }
                    )
                    4 -> AiAssistantScreen(
                        messages = chatMessages,
                        isThinking = isAssistantThinking,
                        onSendMessage = { viewModel.sendAssistantMessage(it) }
                    )
                    5 -> ProfileAndAdminScreen(
                        userPreferences = userPreferences,
                        onPreferencesChange = { viewModel.updateUserPreferences(it) },
                        providersState = providersState
                    )
                }
            }
        }
    }

    // Extracted Filters Dialog (Section 4: «من اینو از حرفت فهمیدم»)
    if (showFilterDialog && extractedFilters != null) {
        ExtractedFiltersDialog(
            filters = extractedFilters!!,
            onConfirm = { updated -> viewModel.confirmExtractedFiltersAndSearch(updated) },
            onDismiss = { viewModel.dismissFilterDialog() }
        )
    }

    // Search Agent Animation Progress (Section 7)
    if (showAgentProgress) {
        SearchAgentProgressDialog(
            steps = agentSteps,
            foundCount = agentFoundCount,
            onDismiss = {}
        )
    }

    // Share Dialog (Section 28)
    if (shareText != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissShareDialog() },
            title = { Text("اشتراک‌گذاری خلاصه آگهی", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = shareText!!,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Pidayab Share", shareText)
                        clipboard.setPrimaryClip(clip)
                        viewModel.dismissShareDialog()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary)
                ) {
                    Text("کپی در کلیپ‌بورد", color = Color(0xFF00382F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissShareDialog() }) {
                    Text("بستن")
                }
            }
        )
    }

    // Voice Search Dialog
    if (showVoiceDialog) {
        VoiceSearchDialog(
            onDismiss = { viewModel.closeVoiceSearch() },
            onVoiceTranscribed = { text ->
                viewModel.performVoiceSearch(text)
            }
        )
    }

    // CameraX Live Capture Screen
    if (showCameraScreen) {
        CameraSearchScreen(
            onClose = { viewModel.closeCameraSearch() },
            onImageCapturedForSearch = { tag ->
                viewModel.closeCameraSearch()
                viewModel.performVisualSearch(tag)
            }
        )
    }
}
