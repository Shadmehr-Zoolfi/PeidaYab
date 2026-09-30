package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ProductItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    itemsToCompare: List<ProductItem>,
    onRemoveItem: (ProductItem) -> Unit,
    onBack: () -> Unit,
    onViewDetail: (ProductItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("میز مقایسه هوشمند آگهی‌ها", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (itemsToCompare.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("empty_comparison"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "هیچ کالایی برای مقایسه انتخاب نشده است.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "از صفحه جستجو یا جزئیات آگهی، دکمه «مقایسه» را بزنید تا تفاوت‌های فنی، قیمت و ریسک را کنار هم مقایسه کنید.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("comparison_table"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header items card row
                item {
                    Text(
                        text = "آیتم‌های در حال مقایسه (${itemsToCompare.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(itemsToCompare) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .width(220.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                    ) {
                                        AsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = item.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        IconButton(
                                            onClick = { onRemoveItem(item) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(28.dp)
                                                .background(Color(0xAA000000), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "حذف", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(item.title, maxLines = 1, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(item.formattedPrice, color = PidayabPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(
                                        onClick = { onViewDetail(item) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("مشاهده جزئیات", fontSize = 11.sp, color = PidayabAccent)
                                    }
                                }
                            }
                        }
                    }
                }

                // Comparison Attribute Rows
                item {
                    ComparisonSectionTitle("شاخص‌های هوش مصنوعی پیدایاب")
                }

                item {
                    ComparisonRow("امتیاز کلی", itemsToCompare.map { "${it.overallScore}/۱۰۰" }, isHighlight = true)
                }
                item {
                    ComparisonRow("امتیاز ریسک", itemsToCompare.map { "${it.riskScore}/۱۰۰ (${it.riskLabel})" })
                }
                item {
                    ComparisonRow("تطابق با درخواست", itemsToCompare.map { "${it.matchScore}٪" })
                }
                item {
                    ComparisonRow("ارزش خرید", itemsToCompare.map { "${it.valueScore}/۱۰۰" })
                }
                item {
                    ComparisonRow("اطمینان اطلاعات", itemsToCompare.map { "${it.confidenceScore}٪" })
                }

                item {
                    ComparisonSectionTitle("اطلاعات عمومی و مکانی")
                }

                item {
                    ComparisonRow("قیمت نهایی", itemsToCompare.map { it.formattedPrice })
                }
                item {
                    ComparisonRow("موقعیت جغرافیایی", itemsToCompare.map { it.location })
                }
                item {
                    ComparisonRow("فروشنده", itemsToCompare.map { "${it.seller} (${it.sellerType})" })
                }
                item {
                    ComparisonRow("منبع آگهی", itemsToCompare.map { it.source })
                }

                // Section 21: خلاصه مقایسه (Comparison Summary)
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PidayabPrimary.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PidayabPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PidayabPrimary)
                                Text("خلاصه و پیشنهاد هوش مصنوعی پیدایاب", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PidayabPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val bestOption = itemsToCompare.maxByOrNull { it.overallScore }
                            val lowestRisk = itemsToCompare.minByOrNull { it.riskScore }
                            val bestPrice = itemsToCompare.minByOrNull { it.price }

                            Text(
                                text = "• بهترین انتخاب مجموع: «${bestOption?.title ?: ""}» به دلیل بالاترین تطابق و کارکرد پایین در شهر شما.\n" +
                                        "• امن‌ترین گزینه: «${lowestRisk?.title ?: ""}» با امتیاز ریسک ${lowestRisk?.riskScore ?: 0}/۱۰۰ به دلیل برگه کارشناسی معتبر.\n" +
                                        "• اقتصادی‌ترین گزینه: «${bestPrice?.title ?: ""}» با قیمت ${bestPrice?.formattedPrice ?: ""}.",
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = PidayabAccent,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun ComparisonRow(label: String, values: List<String>, isHighlight: Boolean = false) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) PidayabPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                values.forEach { v ->
                    Text(
                        text = v,
                        fontSize = 12.sp,
                        fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                        color = if (isHighlight) PidayabPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
