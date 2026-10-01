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
import com.example.model.ExtractedFilters
import com.example.model.ProductItem
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun UniversalSearchScreen(
    initialQuery: String,
    searchResults: List<ProductItem>,
    extractedFilters: ExtractedFilters?,
    onSearchTriggered: (String, ExtractedFilters?) -> Unit,
    onVisualSearch: (String) -> Unit,
    onVideoSearch: (String) -> Unit,
    onFileSearch: (String) -> Unit,
    onOpenFilterDialog: () -> Unit,
    onViewDetail: (ProductItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onCompare: (ProductItem) -> Unit,
    onShare: (ProductItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var queryText by remember { mutableStateOf(initialQuery) }
    var selectedFilterTab by remember { mutableStateOf("بهترین تطابق") }
    var selectedInputMode by remember { mutableStateOf(0) } // 0: Text, 1: Image, 2: Video/Reel, 3: File
    var reelUrlInput by remember { mutableStateOf("") }

    val filterChips = listOf(
        "همه",
        "بهترین تطابق",
        "بهترین قیمت",
        "کمریسکترین",
        "بهترین ارزش خرید",
        "فقط کم‌ریسک",
        "نزدیک شما"
    )

    // Filter and sort items according to selected smart filter
    val displayedResults = remember(searchResults, selectedFilterTab) {
        when (selectedFilterTab) {
            "بهترین تطابق" -> searchResults.sortedByDescending { it.matchScore }
            "بهترین قیمت" -> searchResults.sortedBy { it.price }
            "کمریسکترین" -> searchResults.sortedBy { it.riskScore }
            "بهترین ارزش خرید" -> searchResults.sortedByDescending { it.valueScore }
            "فقط کم‌ریسک" -> searchResults.filter { it.riskScore <= 25 }
            "نزدیک شما" -> searchResults.filter { it.location.contains("تبریز") }
            else -> searchResults
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("universal_search_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Search Bar & Input Mode Switcher
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Mode Tabs (Text, Image, Video, File)
                TabRow(
                    selectedTabIndex = selectedInputMode,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = PidayabPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedInputMode == 0,
                        onClick = { selectedInputMode = 0 },
                        text = { Text("متن", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.EditNote, contentDescription = null, Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedInputMode == 1,
                        onClick = { selectedInputMode = 1 },
                        text = { Text("تصویر", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedInputMode == 2,
                        onClick = { selectedInputMode = 2 },
                        text = { Text("ویدیو/Reel", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.SmartDisplay, contentDescription = null, Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedInputMode == 3,
                        onClick = { selectedInputMode = 3 },
                        text = { Text("فایل", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AttachFile, contentDescription = null, Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selectedInputMode) {
                    0 -> { // Text NLP Search
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = PidayabPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                TextField(
                                    value = queryText,
                                    onValueChange = { queryText = it },
                                    placeholder = {
                                        Text(
                                            "مثال: کرولا کراس هیبرید ۲۰۲۵ زیر ۷ میلیارد تبریز",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("search_query_input")
                                )
                                Button(
                                    onClick = { onSearchTriggered(queryText, null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("execute_search_btn")
                                ) {
                                    Text("پیدا کن", color = Color(0xFF00382F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    1 -> { // Visual Search
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "جستجوی تصویری با تحلیل هوش مصنوعی اشیاء (حالت آزمایشی)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "عکس بگیرید یا از نمونه‌ها انتخاب کنید (حفظ حریم خصوصی: بدون چهره/هویت فردی)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onVisualSearch("کرولا کراس") },
                                    colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("عکس خودرو", fontSize = 12.sp, color = Color(0xFF00382F))
                                }
                                OutlinedButton(
                                    onClick = { onVisualSearch("کفش نایک") },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Checkroom, contentDescription = null, Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("عکس کفش", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = { onVisualSearch("آیفون") },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhoneIphone, contentDescription = null, Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("موبایل", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    2 -> { // Video / Reel URL search (Section 18)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "پیدا کردن از روی ویدیو و شبکه‌های اجتماعی (حالت آزمایشی)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "آدرس ویدیو را وارد کنید (در حالت پیش‌نمایش، فریم‌های کاتالوگ آزمایشی تحلیل می‌شوند):",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = reelUrlInput,
                                    onValueChange = { reelUrlInput = it },
                                    placeholder = { Text("https://instagram.com/reel/...", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { onVideoSearch(reelUrlInput.ifBlank { "Reel کفش نایک" }) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PidayabTertiary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("تحلیل فریم‌ها", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    3 -> { // File Catalog search (Section 19)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "تحلیل هوشمند فایل و استخراج جدول مشخصات/قیمت (حالت آزمایشی)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "پشتیبانی از PDF و اکسل (استخراج و تطابق با کاتالوگ آزمایشی خودرو و کالا)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onFileSearch("لیست_قیمت_خودرو_وارداتی.pdf") },
                                    colors = ButtonDefaults.buttonColors(containerColor = PidayabAccent),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("آپلود کاتالوگ خودرو", fontSize = 12.sp, color = Color.Black)
                                }
                                OutlinedButton(
                                    onClick = { onFileSearch("لیست_موجودی.xlsx") },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("فایل اکسل قیمت", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Extracted Filters preview pill
                if (extractedFilters != null && (!extractedFilters.brand.isNullOrEmpty() || !extractedFilters.model.isNullOrEmpty())) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = PidayabPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PidayabPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenFilterDialog() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = PidayabPrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "فهمیده شد: ${extractedFilters.brand ?: ""} ${extractedFilters.model ?: ""} | سقف: ${extractedFilters.priceMax?.let { "${it / 1_000_000_000L} میلیارد" } ?: "-"} | ${extractedFilters.location ?: ""}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PidayabPrimary
                                )
                            }
                            Text(
                                text = "ویرایش فیلترها",
                                fontSize = 11.sp,
                                color = PidayabAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Smart Filters Row (Section 15)
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterChips) { chip ->
                    val isSelected = selectedFilterTab == chip
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterTab = chip },
                        label = {
                            Text(
                                text = chip,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PidayabPrimary,
                            selectedLabelColor = Color(0xFF00382F)
                        )
                    )
                }
            }
        }

        // Market Overview / Results Count Banner
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${displayedResults.size} نتیجه با اطمینان بالا یافت شد",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "محدوده قیمت: ۶.۵ تا ۷.۴ میلیارد تومان",
                        fontSize = 11.sp,
                        color = PidayabAccent
                    )
                }
            }
        }

        // Search Results List
        if (displayedResults.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "نتیجه‌ای با فیلترهای انتخابی یافت نشد.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(displayedResults) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
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
