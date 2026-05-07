package com.bailing.lark.roll.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bailing.lark.roll.data.UtilityRecord
import com.bailing.lark.roll.ui.components.BeigeOutlinedField
import com.bailing.lark.roll.ui.components.ScreenHeader
import com.bailing.lark.roll.ui.components.SectionHeader
import com.bailing.lark.roll.ui.components.sanitizeDecimal
import com.bailing.lark.roll.ui.icons.UtilityIcon
import com.bailing.lark.roll.ui.icons.UtilityIconKind
import com.bailing.lark.roll.ui.theme.BeigeBorder
import com.bailing.lark.roll.ui.theme.BeigeBorderStrong
import com.bailing.lark.roll.ui.theme.PrimaryAccent
import com.bailing.lark.roll.ui.theme.PrimaryAccentMuted
import com.bailing.lark.roll.ui.theme.SuccessAccent
import com.bailing.lark.roll.ui.theme.SuccessSoft
import com.bailing.lark.roll.ui.theme.TextPrimary
import com.bailing.lark.roll.ui.theme.TextSecondary
import com.bailing.lark.roll.ui.theme.paletteFor

@Composable
fun CalculatorScreen(
    records: SnapshotStateList<UtilityRecord>,
    onUpdate: (UtilityRecord) -> Unit,
    contentPadding: PaddingValues
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val recordsSnapshot = records.toList()
    val totalGross = remember(recordsSnapshot) { recordsSnapshot.sumOf { it.lastMonthCost } }
    val totalDiscount = remember(recordsSnapshot) { recordsSnapshot.sumOf { it.discountAmount } }
    val totalNet = remember(recordsSnapshot) { recordsSnapshot.sumOf { it.finalCost } }
    val readyCount = remember(recordsSnapshot) { recordsSnapshot.count { it.readings.size >= 2 } }

    val pageCount = records.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
    ) {
        ScreenHeader(
            eyebrow = "Estimate by tariff",
            title = "Calculator"
        )
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
            TotalCard(
                totalGross = totalGross,
                totalDiscount = totalDiscount,
                totalNet = totalNet,
                readyCount = readyCount,
                totalCount = pageCount,
                visible = visible
            )
        }

        if (pageCount == 0) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            val currentRecord = records.getOrNull(pagerState.currentPage)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "Per utility", modifier = Modifier.weight(1f))
                if (currentRecord != null) {
                    val pal = paletteFor(currentRecord.kind)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(pal.soft.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1} / $pageCount",
                            style = MaterialTheme.typography.labelMedium,
                            color = pal.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp),
                pageSpacing = 14.dp
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    CalculatorCard(
                        record = records[page],
                        onUpdate = onUpdate,
                        visible = visible
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            PagerIndicator(
                pageCount = pageCount,
                currentPage = pagerState.currentPage,
                records = records,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
        Spacer(
            modifier = Modifier.height(contentPadding.calculateBottomPadding() + 12.dp)
        )
    }
}

@Composable
private fun TotalCard(
    totalGross: Double,
    totalDiscount: Double,
    totalNet: Double,
    readyCount: Int,
    totalCount: Int,
    visible: Boolean
) {
    val anim by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(520),
        label = "total"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = PrimaryAccent.copy(alpha = 0.16f),
                spotColor = PrimaryAccent.copy(alpha = 0.16f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Total this month",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$ ${formatMoney(totalNet * anim.toDouble())}",
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                if (totalDiscount > 0.0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Saved $ ${formatMoney(totalDiscount)} of $ ${formatMoney(totalGross)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryAccentMuted.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                UtilityIcon(
                    kind = UtilityIconKind.Calculator,
                    tint = PrimaryAccent,
                    size = 28.dp,
                    strokeWidth = 1.8.dp
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReadyChip(
                label = "Ready",
                value = "$readyCount / $totalCount",
                accent = SuccessAccent,
                soft = SuccessSoft,
                modifier = Modifier.weight(1f)
            )
            ReadyChip(
                label = "Discounts",
                value = "$ ${formatMoney(totalDiscount)}",
                accent = if (totalDiscount > 0) SuccessAccent else TextSecondary,
                soft = if (totalDiscount > 0) SuccessSoft else BeigeBorder,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ReadyChip(
    label: String,
    value: String,
    accent: Color,
    soft: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(soft.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CalculatorCard(
    record: UtilityRecord,
    onUpdate: (UtilityRecord) -> Unit,
    visible: Boolean
) {
    val palette = paletteFor(record.kind)
    val unit = record.kind.unit.ifEmpty { "units" }
    val ready = record.readings.size >= 2

    var discountDraft by remember(record.discountPercent) {
        mutableStateOf(formatPercent(record.discountPercent))
    }

    val anim by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(420),
        label = "calc-card"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = palette.primary.copy(alpha = 0.16f),
                spotColor = palette.primary.copy(alpha = 0.16f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(22.dp))
    ) {
        // Tinted header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            palette.soft.copy(alpha = 0.55f),
                            palette.soft.copy(alpha = 0.20f)
                        )
                    )
                )
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, palette.primary.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                UtilityIcon(
                    kind = record.kind.iconKind,
                    tint = palette.primary,
                    size = 24.dp,
                    strokeWidth = 2.0.dp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.kind.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = record.providerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.primary.copy(alpha = 0.18f))
                    .border(1.dp, palette.primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$ ${formatMoney(record.tariff)} / ${if (unit == "units") "u" else unit}",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Stacked metrics
        Column(modifier = Modifier.padding(18.dp)) {
            MetricRow(
                label = "Used",
                value = if (ready) "${formatNumber(record.lastMonthUsage)} $unit" else "—",
                palette = palette,
                emphasize = false,
                animationProgress = anim
            )
            Spacer(modifier = Modifier.height(8.dp))
            MetricRow(
                label = "Tariff",
                value = "$ ${formatMoney(record.tariff)} / ${if (unit == "units") "unit" else unit}",
                palette = palette,
                emphasize = false,
                animationProgress = anim
            )
            Spacer(modifier = Modifier.height(8.dp))
            MetricRow(
                label = "Total (before discount)",
                value = "$ ${formatMoney(record.lastMonthCost)}",
                palette = palette,
                emphasize = !ready,
                animationProgress = anim,
                muted = !ready
            )

            if (!ready) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.soft.copy(alpha = 0.55f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Add at least 2 readings on the details screen to see numbers.",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BeigeBorderStrong.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(18.dp))

            DiscountSection(
                discountDraft = discountDraft,
                onDraftChange = { txt ->
                    val cleaned = sanitizeDecimal(txt)
                    discountDraft = cleaned
                    val parsed = cleaned.toDoubleOrNull()?.coerceIn(0.0, 100.0)
                    if (parsed != null) {
                        onUpdate(record.copy(discountPercent = parsed))
                    } else if (cleaned.isEmpty()) {
                        onUpdate(record.copy(discountPercent = 0.0))
                    }
                },
                palette = palette
            )

            Spacer(modifier = Modifier.height(14.dp))

            DiscountResultRow(
                label = "Discount",
                value = "− $ ${formatMoney(record.discountAmount)}",
                description = if (record.discountPercent > 0)
                    "${formatPercent(record.discountPercent)}% off the gross amount"
                else
                    "No discount applied",
                accent = SuccessAccent,
                soft = SuccessSoft
            )
            Spacer(modifier = Modifier.height(10.dp))
            DiscountResultRow(
                label = "With discount",
                value = "$ ${formatMoney(record.finalCost)}",
                description = if (record.discountPercent > 0)
                    "Final amount after the ${formatPercent(record.discountPercent)}% discount"
                else
                    "Same as the gross total",
                accent = palette.primary,
                soft = palette.soft,
                emphasize = true
            )
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    palette: com.bailing.lark.roll.ui.theme.UtilityPalette,
    emphasize: Boolean,
    animationProgress: Float,
    muted: Boolean = false
) {
    val targetTint = when {
        muted -> TextSecondary
        emphasize -> palette.primary
        else -> TextPrimary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (emphasize) palette.soft.copy(alpha = 0.55f)
                else BeigeBorder.copy(alpha = 0.30f)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
            color = targetTint,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.width(animationProgress.let { (4 * it).dp }))
    }
}

@Composable
private fun DiscountSection(
    discountDraft: String,
    onDraftChange: (String) -> Unit,
    palette: com.bailing.lark.roll.ui.theme.UtilityPalette
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(palette.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Discount",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "0–100%",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BeigeOutlinedField(
                value = discountDraft,
                onValueChange = onDraftChange,
                placeholder = "0",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.primary.copy(alpha = 0.18f))
                    .border(1.dp, palette.primary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "%",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DiscountResultRow(
    label: String,
    value: String,
    description: String,
    accent: Color,
    soft: Color,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (emphasize) soft.copy(alpha = 0.55f)
                else BeigeBorder.copy(alpha = 0.30f)
            )
            .border(
                1.dp,
                if (emphasize) accent.copy(alpha = 0.45f) else BeigeBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Text(
            text = value,
            style = if (emphasize) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    records: SnapshotStateList<UtilityRecord>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isActive = i == currentPage
            val palette = paletteFor(records[i].kind)
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = tween(260),
                label = "ind-w-$i"
            )
            val color by animateColorAsState(
                targetValue = if (isActive) palette.primary else BeigeBorderStrong,
                animationSpec = tween(260),
                label = "ind-c-$i"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

private fun formatMoney(value: Double): String =
    "%.2f".format(java.util.Locale.US, value)

private fun formatNumber(value: Double): String {
    if (value == 0.0) return "0"
    if (value == value.toLong().toDouble() && kotlin.math.abs(value) < 1e9) return value.toLong().toString()
    return "%.2f".format(java.util.Locale.US, value)
}

private fun formatPercent(value: Double): String {
    if (value == 0.0) return "0"
    if (value == value.toLong().toDouble()) return value.toLong().toString()
    return "%.2f".format(java.util.Locale.US, value).trimEnd('0').trimEnd('.')
}
