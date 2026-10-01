package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProductItem
import com.example.ui.components.DealBadge
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    featuredItems: List<ProductItem>,
    recentSearches: List<String>,
    savedSearches: List<String>,
    onSearchSubmit: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onVisualSearchClick: () -> Unit,
    onVideoSearchClick: () -> Unit,
    onFileSearchClick: () -> Unit,
    onViewDetail: (ProductItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onCompare: (ProductItem) -> Unit,
    onShare: (ProductItem) -> Unit,
    roomSearchHistory: List<com.example.data.local.SearchHistoryEntity> = emptyList(),
    onOpenVoiceSearch: () -> Unit = {},
    onOpenCameraSearch: () -> Unit = {},
    onDeleteHistoryItem: (Long) -> Unit = {},
    onClearAllHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val exampleSearches = listOf(
        "یه کرولا کراس هیبرید ۲۰۲۵ زیر ۷ میلیارد پیدا کن",
        "این کفش رو پیدا کن",
        "یه مدل مشابه ولی ارزونتر پیدا کن",
        "کمریسکترین آگهی رو پیدا کن"
    )

    val categories = listOf(
        Triple("خودرو", Icons.Default.DirectionsCar, PidayabPrimary),
        Triple("کالای دیجیتال", Icons.Default.PhoneIphone, ScoreGood),
        Triple("کفش و پوشاک", Icons.Default.Checkroom, PidayabTertiary),
        Triple("ساعت و اکسسوری", Icons.Default.Watch, PidayabAccent),
        Triple("لوازم خانگی", Icons.Default.Kitchen, Color(0xFF9C27B0)),
        Triple("ابزار و تجهیزات", Icons.Default.Construction, Color(0xFF607D8B))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PidayabDarkSurfaceVariant,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PidayabPrimary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PidayabPrimary.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PidayabPrimary,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "پیدایاب",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PidayabPrimary
                                )
                                Text(
                                    text = "Pidayab AI",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Demo tag
                        Surface(
                            color = PidayabDarkSurface,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PidayabAccent.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(PidayabAccent)
                                )
                                Text(
                                    text = "حالت آزمایشی",
                                    fontSize = 11.sp,
                                    color = PidayabAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "هر چیزی رو که میبینی، پیدا کن.",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "عکس بفرست، توضیح بده، ویدیو آپلود کن یا فایل بده؛ پیدایاب برات جستجو و مقایسه میکنه.",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Universal Search Input Box
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PidayabPrimary.copy(alpha = 0.4f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = PidayabPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = {
                                        Text(
                                            text = "دنبال چی میگردی؟ (به زبان عامیانه بنویس)",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("home_search_input"),
                                    singleLine = true
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = onOpenVoiceSearch,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("home_mic_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "جستجوی صوتی",
                                            tint = PidayabPrimary
                                        )
                                    }

                                    IconButton(
                                        onClick = onOpenCameraSearch,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("home_camera_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = "اسکن با دوربین",
                                            tint = PidayabAccent
                                        )
                                    }

                                    if (searchQuery.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                val query = searchQuery
                                                searchQuery = ""
                                                onSearchSubmit(query)
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(PidayabPrimary)
                                                .testTag("home_search_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "جستجو",
                                                tint = Color(0xFF00382F)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5 Main Action Buttons with Voice & Camera
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ActionCard(
                            title = "جستجوی صوتی",
                            icon = Icons.Default.Mic,
                            color = PidayabPrimary,
                            modifier = Modifier.weight(1f),
                            onClick = onOpenVoiceSearch
                        )
                        ActionCard(
                            title = "دوربین CameraX",
                            icon = Icons.Default.PhotoCamera,
                            color = PidayabAccent,
                            modifier = Modifier.weight(1f),
                            onClick = onOpenCameraSearch
                        )
                        ActionCard(
                            title = "جستجوی تصویری",
                            icon = Icons.Default.CenterFocusWeak,
                            color = ScoreGood,
                            modifier = Modifier.weight(1f),
                            onClick = onVisualSearchClick
                        )
                        ActionCard(
                            title = "ویدیو و ریلز",
                            icon = Icons.Default.SmartDisplay,
                            color = PidayabTertiary,
                            modifier = Modifier.weight(1f),
                            onClick = onVideoSearchClick
                        )
                        ActionCard(
                            title = "آپلود فایل",
                            icon = Icons.Default.Description,
                            color = PidayabAccent,
                            modifier = Modifier.weight(1f),
                            onClick = onFileSearchClick
                        )
                    }
                }
            }
        }

        // Example Natural Language Requests
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "نمونه درخواست‌های هوشمند:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exampleSearches) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { onSearchSubmit(prompt) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = PidayabAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = prompt,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Categories
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "دسته‌بندی‌های اصلی",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { (name, icon, color) ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clickable { onCategoryClick(name) }
                                .width(110.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent & Saved Searches Section (Room Database Persistence)
        if (roomSearchHistory.isNotEmpty() || recentSearches.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = PidayabPrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "تاریخچه جستجوهای محلی (پایگاه داده Room)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        if (roomSearchHistory.isNotEmpty()) {
                            Text(
                                text = "پاک‌سازی",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onClearAllHistory() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (roomSearchHistory.isNotEmpty()) {
                            items(roomSearchHistory.take(8)) { entry ->
                                val isVehicle = entry.searchType == "VEHICLE"
                                val isVoice = entry.searchType == "VOICE"
                                val isCamera = entry.searchType == "CAMERA"
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isVehicle) PidayabPrimary.copy(alpha = 0.5f)
                                        else if (isVoice) PidayabAccent.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.clickable { onSearchSubmit(entry.query) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isVehicle -> Icons.Default.DirectionsCar
                                                isVoice -> Icons.Default.Mic
                                                isCamera -> Icons.Default.PhotoCamera
                                                else -> Icons.Default.ShoppingBag
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                isVehicle -> PidayabPrimary
                                                isVoice -> PidayabAccent
                                                isCamera -> ScoreGood
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Column {
                                            Text(
                                                text = entry.query,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = when {
                                                        isVehicle -> "خودرو"
                                                        isVoice -> "صوتی"
                                                        isCamera -> "تصویر"
                                                        else -> "کالا"
                                                    },
                                                    fontSize = 10.sp,
                                                    color = PidayabPrimary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (entry.resultCount > 0) {
                                                    Text(
                                                        text = "• ${entry.resultCount} نتیجه",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                        IconButton(
                                            onClick = { onDeleteHistoryItem(entry.id) },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "حذف",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            items(recentSearches.take(4)) { search ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { onSearchSubmit(search) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = search,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Smart Recommendations & Best Deals
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = PidayabAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "پیشنهادهای هوشمند و بهترین آگهی‌ها",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(featuredItems) { item ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                ProductCard(
                    item = item,
                    onViewDetail = onViewDetail,
                    onToggleBookmark = onToggleBookmark,
                    onCompare = onCompare,
                    onAnalyze = onViewDetail,
                    onShare = onShare
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
