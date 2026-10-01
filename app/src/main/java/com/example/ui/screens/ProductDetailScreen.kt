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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ProductItem
import com.example.ui.components.DealBadge
import com.example.ui.components.RiskGauge
import com.example.ui.components.ScoreBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    item: ProductItem,
    onBack: () -> Unit,
    onCompare: (ProductItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onSetPriceAlert: (ProductItem) -> Unit,
    onShare: (ProductItem) -> Unit,
    onOpenSourceUrl: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "تحلیل تخصصی آگهی",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward, // RTL back arrow
                            contentDescription = "بازگشت"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleBookmark(item.id) }) {
                        Icon(
                            imageVector = if (item.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "ذخیره",
                            tint = if (item.isBookmarked) PidayabPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { onShare(item) }) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "اشتراک‌گذاری"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onCompare(item) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary)
                    ) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null, tint = Color(0xFF00382F))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن به مقایسه", color = Color(0xFF00382F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { onSetPriceAlert(item) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PidayabAccent)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("هشدار کاهش قیمت", fontSize = 12.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("product_detail_screen"),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Hero Image & Badges
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0x99000000)),
                                    startY = 120f
                                )
                            )
                    )
                    if (item.isDemo) {
                        Surface(
                            color = Color(0xDD000000),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "حالت آزمایشی",
                                color = PidayabAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Deal badge bottom
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (item.isBestChoice) DealBadge("بهترین انتخاب", PidayabPrimary, Icons.Default.EmojiEvents)
                        if (item.isLowestRisk) DealBadge("کم‌ریسک‌ترین", RiskVeryLow, Icons.Default.VerifiedUser)
                        if (item.isBestPrice) DealBadge("بهترین قیمت", ScoreGood, Icons.Default.Savings)
                    }
                }
            }

            // Title, Price and Location Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.formattedPrice,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PidayabPrimary
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PidayabTertiary, modifier = Modifier.size(14.dp))
                                Text(item.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Seller & Source Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PidayabAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = PidayabAccent)
                                }
                                Column {
                                    Text(item.seller, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("نوع: ${item.sellerType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("منبع: ${item.source}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { onOpenSourceUrl(item.sourceUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, Modifier.size(13.dp), tint = Color(0xFF00382F))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مشاهده آگهی", fontSize = 11.sp, color = Color(0xFF00382F), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Independent Multi-Scores Section (Section 9)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "سیستم امتیازدهی چندبعدی پیدایاب (۰ تا ۱۰۰)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (item.hasSufficientDataForScore) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScoreBadge("امتیاز کلی", item.overallScore, Modifier.weight(1f))
                            ScoreBadge("تطابق", item.matchScore, Modifier.weight(1f))
                            ScoreBadge("ارزش خرید", item.valueScore, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScoreBadge("کیفیت آگهی", item.qualityScore, Modifier.weight(1f))
                            ScoreBadge("قیمت منصفانه", item.priceScore, Modifier.weight(1f))
                            ScoreBadge("اطمینان اطلاعات", item.confidenceScore, Modifier.weight(1f))
                        }
                    } else {
                        Text(
                            text = "اطلاعات کافی برای امتیازدهی دقیق وجود ندارد.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // AI Risk Analyzer Gauge & Observable Warning Signs (Section 10 & 11)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    RiskGauge(riskScore = item.riskScore, confidenceScore = item.confidenceScore)

                    if (item.warnings.isNotEmpty() || item.riskFactors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.riskScore > 50) RiskHigh.copy(alpha = 0.1f) else PidayabDarkSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (item.riskScore > 50) RiskHigh.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.riskScore > 50) Icons.Default.Warning else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (item.riskScore > 50) RiskHigh else PidayabAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "نکات قابل بررسی و علائم ریسک",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (item.riskScore > 50) RiskHigh else PidayabAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                (item.warnings + item.riskFactors).forEach { warning ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("•", color = if (item.riskScore > 50) RiskHigh else PidayabAccent, fontWeight = FontWeight.Bold)
                                        Text(warning, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Price Analysis & Market Range (Section 12)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "تحلیل قیمت در بازار",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (item.hasSufficientDataForPriceAnalysis) {
                                Text(
                                    text = "محدوده قیمت مشاهده‌شده در بازار: ۶.۵ تا ۷.۴ میلیارد تومان",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PidayabPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "این آگهی نسبت به میانگین بازار حدود ۲.۳٪ منصفانه‌تر قیمت‌گذاری شده است.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "اطلاعات کافی برای تحلیل قیمت وجود ندارد.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (item.historicalPrices.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "روند قیمت مشاهده‌شده قبلی:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    item.historicalPrices.forEach { hist ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(hist.dateLabel, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(hist.formattedPrice, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PidayabPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pros & Highlights
            if (item.pros.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "مزایای آگهی",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreExcellent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        item.pros.forEach { pro ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ScoreExcellent, modifier = Modifier.size(16.dp))
                                Text(pro, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Technical Specifications Table
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "مشخصات فنی و اطلاعات آگهی",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            item.specs.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = value,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            }
                        }
                    }
                }
            }

            // Source vs AI Distinction Note (Section 22 & 44)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "شفافیت داده‌های پیدایاب:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PidayabPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• اطلاعات منبع: قیمت، مشخصات، شهر و عکس مستقیماً از آگهی ${item.source} خوانده شده است.\n• تحلیل هوش مصنوعی: امتیاز ریسک و ارزش خرید بر مبنای مقایسه آماری و شواهد آگهی محاسبه شده است.",
                            fontSize = 11.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
