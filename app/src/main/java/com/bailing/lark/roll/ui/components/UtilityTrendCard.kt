package com.bailing.lark.roll.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath as clipDrawPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bailing.lark.roll.data.UtilityRecord
import com.bailing.lark.roll.data.monthName
import com.bailing.lark.roll.ui.icons.UtilityIcon
import com.bailing.lark.roll.ui.theme.BeigeBackground
import com.bailing.lark.roll.ui.theme.BeigeBorder
import com.bailing.lark.roll.ui.theme.BeigeBorderStrong
import com.bailing.lark.roll.ui.theme.TextPrimary
import com.bailing.lark.roll.ui.theme.TextSecondary
import com.bailing.lark.roll.ui.theme.UtilityPalette
import com.bailing.lark.roll.ui.theme.paletteFor

private data class TrendPoint(
    val year: Int,
    val month: Int,
    val usage: Double,
    val payment: Double,
    val tariff: Double
)

private fun UtilityRecord.trendPoints(): List<TrendPoint> {
    val sorted = sortedReadings
    if (sorted.size < 2) return emptyList()
    val out = ArrayList<TrendPoint>(sorted.size - 1)
    for (i in 1 until sorted.size) {
        val a = sorted[i - 1]
        val b = sorted[i]
        val usage = (b.value - a.value).coerceAtLeast(0.0)
        out += TrendPoint(
            year = b.year,
            month = b.month,
            usage = usage,
            payment = usage * tariff,
            tariff = tariff
        )
    }
    return out
}

@Composable
fun UtilityTrendCard(
    record: UtilityRecord,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val unit = record.kind.unit.ifEmpty { "units" }
    val palette = paletteFor(record.kind)
    val points = record.trendPoints()
    val latest = points.lastOrNull()

    val totalUsage = points.sumOf { it.usage }
    val totalPayment = points.sumOf { it.payment }
    val avgUsage = if (points.isNotEmpty()) totalUsage / points.size else 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = palette.primary.copy(alpha = 0.18f),
                spotColor = palette.primary.copy(alpha = 0.18f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(24.dp))
    ) {
        // Tinted header strip
        Column(
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
                .padding(18.dp)
        ) {
            TrendHeader(record = record, unit = unit, palette = palette)
            Spacer(modifier = Modifier.height(14.dp))
            LatestStatsRow(
                latest = latest,
                unit = unit,
                palette = palette,
                avgUsage = avgUsage,
                totalPayment = totalPayment
            )
        }

        Column(modifier = Modifier.padding(18.dp)) {
            if (points.isEmpty()) {
                EmptyTrendHint(palette)
            } else {
                BlockTitle(
                    label = "Payment trend",
                    trailing = "$ / month",
                    palette = palette
                )
                Spacer(modifier = Modifier.height(10.dp))
                AreaLineChart(
                    points = points,
                    valueOf = { it.payment },
                    palette = palette,
                    visible = visible,
                    valueFormatter = { "$ ${formatMoney(it)}" },
                    height = 130.dp
                )
                Spacer(modifier = Modifier.height(20.dp))
                BlockTitle(
                    label = "Usage",
                    trailing = "$unit / month",
                    palette = palette
                )
                Spacer(modifier = Modifier.height(10.dp))
                UsageBars(
                    points = points,
                    palette = palette,
                    unit = unit,
                    visible = visible
                )
                Spacer(modifier = Modifier.height(18.dp))
                TariffStrip(record = record, unit = unit, palette = palette)
            }
        }
    }
}

@Composable
private fun TrendHeader(record: UtilityRecord, unit: String, palette: UtilityPalette) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            UtilityIcon(
                kind = record.kind.iconKind,
                tint = palette.primary,
                size = 26.dp,
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
}

@Composable
private fun LatestStatsRow(
    latest: TrendPoint?,
    unit: String,
    palette: UtilityPalette,
    avgUsage: Double,
    totalPayment: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatBlock(
            label = if (latest != null) "Latest · ${monthName(latest.month)} ${latest.year}" else "Latest",
            value = if (latest != null) "$ ${formatMoney(latest.payment)}" else "—",
            sub = if (latest != null) "${formatValue(latest.usage)} $unit" else "no readings",
            tint = palette.primary,
            modifier = Modifier.fillMaxWidth(),
            emphasize = true
        )
        StatBlock(
            label = "Avg usage",
            value = if (latest != null) formatValue(avgUsage) else "—",
            sub = if (latest != null) "$unit / month" else "",
            tint = palette.primary,
            modifier = Modifier.fillMaxWidth(),
            emphasize = false
        )
        StatBlock(
            label = "Total spent",
            value = if (latest != null) "$ ${formatMoney(totalPayment)}" else "—",
            sub = if (latest != null) "across history" else "",
            tint = palette.primary,
            modifier = Modifier.fillMaxWidth(),
            emphasize = false
        )
    }
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    sub: String,
    tint: Color,
    modifier: Modifier = Modifier,
    emphasize: Boolean
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (emphasize) tint.copy(alpha = 0.45f) else BeigeBorder,
                shape = RoundedCornerShape(14.dp)
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
            if (sub.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sub,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
        Text(
            text = value,
            style = if (emphasize) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.titleMedium,
            color = if (emphasize) tint else TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BlockTitle(label: String, trailing: String, palette: UtilityPalette) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(palette.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = trailing,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun AreaLineChart(
    points: List<TrendPoint>,
    valueOf: (TrendPoint) -> Double,
    palette: UtilityPalette,
    visible: Boolean,
    valueFormatter: (Double) -> String,
    height: Dp
) {
    val values = points.map(valueOf)
    val maxValue = values.maxOrNull() ?: 0.0
    val minValue = values.minOrNull() ?: 0.0
    val range = (maxValue - minValue).takeIf { it > 0 } ?: maxValue.takeIf { it > 0 } ?: 1.0
    val animated by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900),
        label = "area"
    )
    val dotHaloColor = MaterialTheme.colorScheme.surface

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEachIndexed { idx, p ->
                val isLast = idx == points.lastIndex
                Text(
                    text = valueFormatter(valueOf(p)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isLast) palette.primary else TextSecondary,
                    fontWeight = if (isLast) FontWeight.SemiBold else FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            // Grid lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = BeigeBorderStrong.copy(alpha = 0.30f)
                val steps = 4
                val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                for (i in 0..steps) {
                    val y = size.height * i / steps
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = dash
                    )
                }
            }
            // Area + curve
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (values.isEmpty()) return@Canvas
                val w = size.width
                val h = size.height
                val verticalPadding = h * 0.10f
                val drawableH = h - verticalPadding * 2

                fun pointFor(idx: Int): Offset {
                    val x = if (values.size == 1) w / 2f
                    else w * idx / (values.size - 1).toFloat()
                    val v = values[idx]
                    val norm = if (range > 0) ((v - minValue) / range).toFloat() else 0.5f
                    val y = verticalPadding + (1f - norm) * drawableH
                    return Offset(x, y)
                }

                val raw = (0 until values.size).map { pointFor(it) }

                // Smooth horizontal-tangent path
                val curve = Path().apply {
                    if (raw.isEmpty()) return@apply
                    moveTo(raw[0].x, raw[0].y)
                    for (i in 1 until raw.size) {
                        val prev = raw[i - 1]
                        val curr = raw[i]
                        val cp1 = Offset(prev.x + (curr.x - prev.x) * 0.5f, prev.y)
                        val cp2 = Offset(curr.x - (curr.x - prev.x) * 0.5f, curr.y)
                        cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, curr.x, curr.y)
                    }
                }

                // Filled area (curve + bottom)
                val area = Path().apply {
                    addPath(curve)
                    if (raw.isNotEmpty()) {
                        lineTo(raw.last().x, h)
                        lineTo(raw.first().x, h)
                        close()
                    }
                }

                // Clip to animation progress for left-to-right reveal
                val revealPath = Path().apply {
                    addRect(
                        androidx.compose.ui.geometry.Rect(
                            offset = Offset.Zero,
                            size = androidx.compose.ui.geometry.Size(w * animated, h)
                        )
                    )
                }

                clipDrawPath(revealPath) {
                    drawPath(
                        path = area,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                palette.primary.copy(alpha = 0.45f),
                                palette.primary.copy(alpha = 0.05f)
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )
                    drawPath(
                        path = curve,
                        color = palette.primary,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Data point dots (don't clip)
                raw.forEachIndexed { idx, point ->
                    val isLast = idx == raw.lastIndex
                    val r = if (isLast) 6.dp.toPx() else 4.dp.toPx()
                    val show = animated >= (idx.toFloat() / raw.size.coerceAtLeast(1))
                    if (show) {
                        drawCircle(
                            color = dotHaloColor,
                            radius = r + 2.5f,
                            center = point
                        )
                        drawCircle(
                            color = palette.primary,
                            radius = r,
                            center = point
                        )
                        if (isLast) {
                            drawCircle(
                                color = palette.primary.copy(alpha = 0.20f),
                                radius = r * 2.2f,
                                center = point
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BeigeBorderStrong)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { p ->
                Text(
                    text = "${monthName(p.month)} ${(p.year % 100).toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun UsageBars(
    points: List<TrendPoint>,
    palette: UtilityPalette,
    unit: String,
    visible: Boolean
) {
    val values = points.map { it.usage }
    val max = values.maxOrNull() ?: 0.0
    val chartHeight = 90.dp
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight + 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { index, v ->
                val target = if (max > 0.0) (v / max).toFloat() else 0f
                val animated by animateFloatAsState(
                    targetValue = if (visible) target else 0f,
                    animationSpec = tween(720, delayMillis = 120 + index * 90),
                    label = "usage-bar-$index"
                )
                val isLast = index == values.lastIndex
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = formatValue(v),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLast) palette.primary else TextSecondary,
                        fontWeight = if (isLast) FontWeight.SemiBold else FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(chartHeight * animated)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    if (isLast) Brush.verticalGradient(
                                        listOf(palette.primary, palette.primary.copy(alpha = 0.55f))
                                    )
                                    else Brush.verticalGradient(
                                        listOf(
                                            palette.soft.copy(alpha = 0.95f),
                                            palette.soft.copy(alpha = 0.45f)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { p ->
                Text(
                    text = monthName(p.month),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Highlighted: $unit used in the latest month.",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun TariffStrip(record: UtilityRecord, unit: String, palette: UtilityPalette) {
    val anim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(620),
        label = "tariff-strip"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.soft.copy(alpha = 0.45f))
            .border(1.dp, palette.primary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Tariff",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$ ${formatMoney(record.tariff)} / ${if (unit == "units") "unit" else unit}",
                style = MaterialTheme.typography.titleSmall,
                color = palette.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(BeigeBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(anim)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(palette.soft, palette.primary)
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Constant rate applied to every month.",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun EmptyTrendHint(palette: UtilityPalette) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.soft.copy(alpha = 0.45f))
            .border(1.dp, palette.primary.copy(alpha = 0.30f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(palette.primary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Add at least two readings to see usage and payment trends.",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
    }
}

private fun formatMoney(value: Double): String =
    "%.2f".format(java.util.Locale.US, value)

private fun formatValue(value: Double): String {
    if (value == 0.0) return "0"
    if (value == value.toLong().toDouble() && kotlin.math.abs(value) < 1e9) return value.toLong().toString()
    return "%.2f".format(java.util.Locale.US, value)
}
