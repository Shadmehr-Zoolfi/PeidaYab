package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.model.PriceAlert
import com.example.model.ProductItem
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun AlertsAndSavedScreen(
    savedItems: List<ProductItem>,
    savedSearches: List<String>,
    priceAlerts: List<PriceAlert>,
    onToggleBookmark: (String) -> Unit,
    onViewDetail: (ProductItem) -> Unit,
    onCompare: (ProductItem) -> Unit,
    onShare: (ProductItem) -> Unit,
    onExecuteSearch: (String) -> Unit,
    onToggleAlert: (String) -> Unit,
    onAddPriceAlert: (PriceAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: آگهی‌های ذخیره‌شده, 1: هشدارهای قیمت, 2: جستجوهای ذخیره
    var showCreateAlertDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("alerts_and_saved_screen")
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
                    text = { Text("نشان‌شده‌ها (${savedItems.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null, Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("هشدارهای قیمت", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("جستجوهای ذخیره", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.SavedSearch, contentDescription = null, Modifier.size(16.dp)) }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                if (savedItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("هنوز هیچ آگهی ذخیره نکرده‌اید.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(savedItems) { item ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
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
            }
            1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showCreateAlertDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = null, tint = Color(0xFF00382F))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ایجاد هشدار قیمت جدید", color = Color(0xFF00382F), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(priceAlerts) { alert ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(alert.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "قیمت هدف شما: ${alert.formattedTargetPrice}",
                                            fontSize = 12.sp,
                                            color = PidayabPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "قیمت فعلی بازار: ${alert.currentMarketPrice}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Switch(
                                        checked = alert.isActive,
                                        onCheckedChange = { onToggleAlert(alert.id) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = PidayabPrimary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(savedSearches) { search ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onExecuteSearch(search) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.SavedSearch, contentDescription = null, tint = PidayabAccent)
                                    Text(search, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        if (showCreateAlertDialog) {
            var alertTitle by remember { mutableStateOf("تویوتا کرولا کراس هیبرید ۲۰۲۵") }
            var alertTargetPrice by remember { mutableStateOf("۶.۵ میلیارد تومان") }

            AlertDialog(
                onDismissRequest = { showCreateAlertDialog = false },
                title = { Text("تنظیم هشدار کاهش قیمت", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("به محض یافتن آگهی جدید زیر این قیمت، به شما اطلاع داده می‌شود:", fontSize = 12.sp)
                        OutlinedTextField(
                            value = alertTitle,
                            onValueChange = { alertTitle = it },
                            label = { Text("عنوان کالا یا خودرو") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = alertTargetPrice,
                            onValueChange = { alertTargetPrice = it },
                            label = { Text("حداکثر قیمت مدنظر") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newAlert = PriceAlert(
                                id = "alert_${System.currentTimeMillis()}",
                                title = alertTitle,
                                targetPrice = 6500000000L,
                                formattedTargetPrice = alertTargetPrice,
                                currentMarketPrice = "۶.۸۵ میلیارد تومان",
                                category = "خودرو",
                                isActive = true,
                                createdDate = "امروز"
                            )
                            onAddPriceAlert(newAlert)
                            showCreateAlertDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary)
                    ) {
                        Text("فعال‌سازی هشدار", color = Color(0xFF00382F), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateAlertDialog = false }) {
                        Text("انصراف")
                    }
                }
            )
        }
    }
}
