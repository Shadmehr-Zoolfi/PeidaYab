package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.ExtractedFilters
import com.example.model.ProductItem
import com.example.model.SearchAgentStep
import com.example.ui.theme.*

@Composable
fun ScoreBadge(
    label: String,
    score: Int,
    modifier: Modifier = Modifier,
    isRisk: Boolean = false
) {
    val bgColor: Color = if (isRisk) {
        when {
            score <= 20 -> RiskVeryLow.copy(alpha = 0.15f)
            score <= 40 -> RiskLow.copy(alpha = 0.15f)
            score <= 60 -> RiskMedium.copy(alpha = 0.15f)
            score <= 80 -> RiskHigh.copy(alpha = 0.15f)
            else -> RiskVeryHigh.copy(alpha = 0.15f)
        }
    } else {
        when {
            score >= 90 -> ScoreExcellent.copy(alpha = 0.15f)
            score >= 75 -> ScoreGood.copy(alpha = 0.15f)
            score >= 50 -> ScoreAverage.copy(alpha = 0.15f)
            else -> ScorePoor.copy(alpha = 0.15f)
        }
    }

    val textColor: Color = if (isRisk) {
        when {
            score <= 20 -> RiskVeryLow
            score <= 40 -> RiskLow
            score <= 60 -> RiskMedium
            score <= 80 -> RiskHigh
            else -> RiskVeryHigh
        }
    } else {
        when {
            score >= 90 -> ScoreExcellent
            score >= 75 -> ScoreGood
            score >= 50 -> ScoreAverage
            else -> ScorePoor
        }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$label: ",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$score/۱۰۰",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun RiskGauge(
    riskScore: Int,
    confidenceScore: Int,
    modifier: Modifier = Modifier
) {
    val riskColor = when {
        riskScore <= 20 -> RiskVeryLow
        riskScore <= 40 -> RiskLow
        riskScore <= 60 -> RiskMedium
        riskScore <= 80 -> RiskHigh
        else -> RiskVeryHigh
    }

    val riskText = when {
        riskScore <= 20 -> "ریسک بسیار پایین"
        riskScore <= 40 -> "ریسک پایین"
        riskScore <= 60 -> "ریسک متوسط"
        riskScore <= 80 -> "ریسک بالا"
        else -> "ریسک بسیار بالا"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, riskColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
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
                    imageVector = if (riskScore <= 40) Icons.Default.Shield else Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = riskColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "ارزیابی ریسک آگهی: $riskText",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = riskColor
                )
            }
            Text(
                text = "$riskScore/۱۰۰",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = riskColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress line
        LinearProgressIndicator(
            progress = { riskScore / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = riskColor,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "اطمینان تحلیل: $confidenceScore٪",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "مبتنی بر شواهد عینی آگهی",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DealBadge(text: String, color: Color, icon: ImageVector) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ProductCard(
    item: ProductItem,
    onViewDetail: (ProductItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onCompare: (ProductItem) -> Unit,
    onAnalyze: (ProductItem) -> Unit,
    onShare: (ProductItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetail(item) }
            .testTag("product_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Image & Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x99000000)),
                                startY = 100f
                            )
                        )
                )

                // Top Left/Right indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Demo or Real Web indicator
                    if (item.isDemo) {
                        Surface(
                            color = Color(0xCC000000),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "حالت آزمایشی",
                                color = PidayabAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xEE00382F),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "نتیجه وب واقعی",
                                color = PidayabPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Bookmark button
                    IconButton(
                        onClick = { onToggleBookmark(item.id) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .testTag("bookmark_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "ذخیره آگهی",
                            tint = if (item.isBookmarked) PidayabPrimary else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Deal highlights badge bottom of image
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (item.isBestChoice) {
                        DealBadge("بهترین انتخاب", PidayabPrimary, Icons.Default.EmojiEvents)
                    }
                    if (item.isBestPrice) {
                        DealBadge("بهترین قیمت", ScoreGood, Icons.Default.Savings)
                    }
                    if (item.isLowestRisk) {
                        DealBadge("کم‌ریسک‌ترین", RiskVeryLow, Icons.Default.VerifiedUser)
                    }
                    if (item.isLocalBest) {
                        DealBadge("نزدیک شما", PidayabTertiary, Icons.Default.LocationOn)
                    }
                }
            }

            // Info Content
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.formattedPrice,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PidayabPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = item.location,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Seller & Source
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = PidayabAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = item.seller,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "(${item.sellerType})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "منبع: ${item.source}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Score Chips Row
                if (item.hasSufficientDataForScore) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ScoreBadge("امتیاز کلی", item.overallScore, Modifier.weight(1f))
                        ScoreBadge("تطابق", item.matchScore, Modifier.weight(1f))
                        ScoreBadge("ریسک", item.riskScore, Modifier.weight(1f), isRisk = true)
                    }
                } else {
                    Text(
                        text = "اطلاعات کافی برای امتیازدهی دقیق وجود ندارد.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onViewDetail(item) },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("view_listing_btn_${item.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PidayabPrimary,
                            contentColor = Color(0xFF00382F)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاهده آگهی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onCompare(item) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("compare_btn_${item.id}"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مقایسه", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = { onShare(item) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "اشتراک‌گذاری",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAgentProgressDialog(
    steps: List<SearchAgentStep>,
    foundCount: Int?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated AI Scanner Icon
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PidayabPrimary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PidayabPrimary,
                        modifier = Modifier.size(32.dp * scale)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "عامل هوشمند پیدایاب",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "دارم بهترین گزینه‌ها رو پیدا می‌کنم...",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    steps.forEach { step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (step.isDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PidayabPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (step.isActive) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = PidayabAccent
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = step.title,
                                fontSize = 13.sp,
                                color = if (step.isDone) MaterialTheme.colorScheme.onSurface
                                else if (step.isActive) PidayabAccent
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = if (step.isActive || step.isDone) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }

                if (foundCount != null) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Surface(
                        color = PidayabPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✓ $foundCount نتیجه با بالاترین امتیاز پیدا شد",
                            color = PidayabPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExtractedFiltersDialog(
    filters: ExtractedFilters,
    onConfirm: (ExtractedFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var brand by remember { mutableStateOf(filters.brand ?: "") }
    var model by remember { mutableStateOf(filters.model ?: "") }
    var year by remember { mutableStateOf(filters.year?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(filters.priceMax?.let { "${it / 1_000_000_000L} میلیارد" } ?: "۷ میلیارد") }
    var location by remember { mutableStateOf(filters.location ?: "تبریز") }
    var fuel by remember { mutableStateOf(filters.fuelType ?: "هیبرید") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = PidayabPrimary)
                Text("من اینو از حرفت فهمیدم:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "می‌توانید هر یک از فیلترهای استخراج‌شده را قبل از جستجو ویرایش کنید:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("برند") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("مدل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("سال مدل") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fuel,
                        onValueChange = { fuel = it },
                        label = { Text("سوخت / پیشرانه") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = maxPrice,
                        onValueChange = { maxPrice = it },
                        label = { Text("سقف قیمت") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("شهر / منطقه") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = filters.copy(
                        brand = brand.ifBlank { null },
                        model = model.ifBlank { null },
                        year = year.toIntOrNull(),
                        location = location.ifBlank { null },
                        fuelType = fuel.ifBlank { null }
                    )
                    onConfirm(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary)
            ) {
                Text("تایید و جستجوی هوشمند", color = Color(0xFF00382F), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
