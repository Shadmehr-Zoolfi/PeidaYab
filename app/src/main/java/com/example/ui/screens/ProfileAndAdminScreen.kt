package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminMetrics
import com.example.model.UserPreferences
import com.example.ui.theme.*

@Composable
fun ProfileAndAdminScreen(
    userPreferences: UserPreferences,
    onPreferencesChange: (UserPreferences) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: پروفایل من و تنظیمات, 1: داشبورد مدیریت
    var city by remember { mutableStateOf(userPreferences.city) }
    var currency by remember { mutableStateOf(userPreferences.currency) }
    var riskLimit by remember { mutableStateOf(userPreferences.maxRiskTolerance.toFloat()) }
    val adminMetrics = remember { AdminMetrics() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen")
    ) {
        // Tab Row
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PidayabPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("پروفایل و ترجیحات", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Person, contentDescription = null, Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("داشبورد مدیریت", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, Modifier.size(16.dp)) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedTab == 0) {
                // User Avatar Header
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(PidayabPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = PidayabPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Column {
                                Text("کاربر هوشمند پیدایاب", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                Text("طرح کاربری: آزمایشی رایگان (Monetization-Ready)", fontSize = 11.sp, color = PidayabAccent)
                                Text("شناسه: pidayab_user_984", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Preferences Form (Section 30 & 31)
                item {
                    Text("ترجیحات کاربری و شخصی‌سازی", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Favorite City
                            Column {
                                Text("شهر پیش‌فرض برای آگهی‌های محلی:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("تبریز", "تهران", "اصفهان", "مشهد", "شیراز").forEach { c ->
                                        FilterChip(
                                            selected = city == c,
                                            onClick = {
                                                city = c
                                                onPreferencesChange(userPreferences.copy(city = c))
                                            },
                                            label = { Text(c, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PidayabPrimary,
                                                selectedLabelColor = Color(0xFF00382F)
                                            )
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            // Currency
                            Column {
                                Text("واحد پول و نمایش مبالغ:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("تومان", "ریال", "دلار").forEach { cur ->
                                        FilterChip(
                                            selected = currency == cur,
                                            onClick = {
                                                currency = cur
                                                onPreferencesChange(userPreferences.copy(currency = cur))
                                            },
                                            label = { Text(cur, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PidayabPrimary,
                                                selectedLabelColor = Color(0xFF00382F)
                                            )
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            // Risk Tolerance Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("حداکثر ریسک قابل قبول:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${riskLimit.toInt()} / ۱۰۰", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PidayabPrimary)
                                }
                                Slider(
                                    value = riskLimit,
                                    onValueChange = {
                                        riskLimit = it
                                        onPreferencesChange(userPreferences.copy(maxRiskTolerance = it.toInt()))
                                    },
                                    valueRange = 10f..80f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = PidayabPrimary,
                                        activeTrackColor = PidayabPrimary
                                    )
                                )
                                Text(
                                    text = "آگهی‌های با ریسک بالاتر از این مقدار به صورت خودکار هشدار خواهند داد.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Privacy Commitment Notice (Section 2 Privacy)
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = PidayabPrimary)
                                Text("تعهدنامه حریم خصوصی و امنیت پیدایاب", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "سامانه پیدایاب برای جستجوی اشخاص، تشخیص چهره یا ابزار تعقیب طراحی نشده است. تمام الگوریتم‌ها صرفاً بر روی کشف محصولات، خودرو، کالاها و اعتبارسنجی شفاف آگهی‌های مجاز متمرکز است.",
                                fontSize = 11.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Admin Dashboard (Section 39)
                item {
                    Text("داشبورد نظارت و آمار سیستم (مدیریت)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(title = "کاربران فعال", value = "${adminMetrics.totalUsers}", icon = Icons.Default.People, color = PidayabPrimary, modifier = Modifier.weight(1f))
                        MetricCard(title = "جستجوهای روزانه", value = "${adminMetrics.dailySearches}", icon = Icons.Default.Search, color = ScoreGood, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(title = "فراخوانی‌های API", value = "${adminMetrics.apiCallsCount}", icon = Icons.Default.CloudSync, color = PidayabAccent, modifier = Modifier.weight(1f))
                        MetricCard(title = "وضعیت سیستم", value = "۹۹.۹٪ پایدار", icon = Icons.Default.CheckCircle, color = RiskVeryLow, modifier = Modifier.weight(1f))
                    }
                }

                // Popular Categories
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("دسته‌بندی‌های پربازدید و پرجستجو", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            adminMetrics.popularCategories.forEach { (cat, count) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cat, fontSize = 12.sp)
                                    Text("$count جستجو", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PidayabPrimary)
                                }
                            }
                        }
                    }
                }

                // Popular Queries
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("بیشترین عبارت‌های جستجوشده امروز", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            adminMetrics.popularQueries.forEach { q ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PidayabAccent, modifier = Modifier.size(16.dp))
                                    Text(q, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Recent System Logs
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("گزارش‌های امنیتی و عملیاتی زنده", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            adminMetrics.recentLogs.forEach { log ->
                                Text("• $log", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
