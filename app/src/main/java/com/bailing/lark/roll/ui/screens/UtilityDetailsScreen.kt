package com.bailing.lark.roll.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import com.bailing.lark.roll.data.MIN_RECORD_YEAR
import com.bailing.lark.roll.data.MeterReading
import com.bailing.lark.roll.data.UtilityKind
import com.bailing.lark.roll.data.UtilityRecord
import com.bailing.lark.roll.data.monthFullName
import com.bailing.lark.roll.data.monthName
import com.bailing.lark.roll.ui.components.BeigeOutlinedField
import com.bailing.lark.roll.ui.components.SectionHeader
import com.bailing.lark.roll.ui.components.sanitizeDecimal
import com.bailing.lark.roll.ui.icons.UtilityIcon
import com.bailing.lark.roll.ui.icons.UtilityIconKind
import com.bailing.lark.roll.ui.theme.BeigeBackground
import com.bailing.lark.roll.ui.theme.BeigeBorder
import com.bailing.lark.roll.ui.theme.BeigeBorderStrong
import com.bailing.lark.roll.ui.theme.PrimaryAccent
import com.bailing.lark.roll.ui.theme.PrimaryAccentMuted
import com.bailing.lark.roll.ui.theme.PrimaryAccentSoft
import com.bailing.lark.roll.ui.theme.SuccessAccent
import com.bailing.lark.roll.ui.theme.SuccessSoft
import com.bailing.lark.roll.ui.theme.TextPrimary
import com.bailing.lark.roll.ui.theme.TextSecondary
import com.bailing.lark.roll.ui.theme.WarningAccent
import com.bailing.lark.roll.ui.theme.WarningSoft

@Composable
fun UtilityDetailsOverlay(
    visible: Boolean,
    record: UtilityRecord?,
    onClose: () -> Unit,
    onUpdate: (UtilityRecord) -> Unit
) {
    AnimatedVisibility(
        visible = visible && record != null,
        enter = fadeIn(tween(200)) + slideInVertically(tween(280)) { it / 12 },
        exit = fadeOut(tween(180)) + slideOutVertically(tween(220)) { it / 12 }
    ) {
        if (record != null) {
            UtilityDetailsScreen(
                record = record,
                onClose = onClose,
                onUpdate = onUpdate
            )
        }
    }
}

@Composable
private fun UtilityDetailsScreen(
    record: UtilityRecord,
    onClose: () -> Unit,
    onUpdate: (UtilityRecord) -> Unit
) {
    val unit = record.kind.unit.ifEmpty { "units" }

    val currentYM = remember { YearMonth.now() }
    val maxYear = maxOf(currentYM.year, MIN_RECORD_YEAR)
    val maxMonthInMaxYear = if (currentYM.year >= MIN_RECORD_YEAR) currentYM.monthValue else 12
    val initialYear = currentYM.year.coerceIn(MIN_RECORD_YEAR, maxYear)
    val initialMonth = if (initialYear == maxYear) maxMonthInMaxYear else 1

    var providerDraft by remember(record.providerName) { mutableStateOf(record.providerName) }
    var tariffDraft by remember(record.tariff) { mutableStateOf(formatPlain(record.tariff)) }

    var pickedYear by remember { mutableIntStateOf(initialYear) }
    var pickedMonth by remember { mutableIntStateOf(initialMonth) }
    var readingDraft by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 38.dp)
            .background(BeigeBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            DetailsTopBar(record = record, onClose = onClose)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    UsageCard(record = record, unit = unit)
                }
                item {
                    SectionHeader(title = "Provider")
                }
                item {
                    BeigeOutlinedField(
                        value = providerDraft,
                        onValueChange = {
                            providerDraft = it
                            onUpdate(record.copy(providerName = it))
                        },
                        placeholder = "Provider name"
                    )
                }
                item {
                    SectionHeader(title = "Tariff")
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BeigeOutlinedField(
                            value = tariffDraft,
                            onValueChange = { txt ->
                                tariffDraft = sanitizeDecimal(txt)
                                tariffDraft.toDoubleOrNull()?.let {
                                    onUpdate(record.copy(tariff = it))
                                }
                            },
                            placeholder = "0.00",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$ per ${if (unit == "units") "unit" else unit}",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                item {
                    Text(
                        text = "Used in calculations on the Calculator tab. Not applied to the listed amount on Overview.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                item {
                    SectionHeader(title = "Add reading")
                }
                item {
                    AddReadingForm(
                        unit = unit,
                        pickedYear = pickedYear,
                        onYearChange = { newYear ->
                            pickedYear = newYear
                            if (newYear == maxYear && pickedMonth > maxMonthInMaxYear) {
                                pickedMonth = maxMonthInMaxYear
                            }
                        },
                        pickedMonth = pickedMonth,
                        onMonthChange = { pickedMonth = it },
                        readingDraft = readingDraft,
                        onReadingDraftChange = { readingDraft = sanitizeDecimal(it) },
                        alreadyExists = record.hasReadingFor(pickedYear, pickedMonth),
                        maxYear = maxYear,
                        maxMonthInMaxYear = maxMonthInMaxYear,
                        onAdd = {
                            val parsed = readingDraft.toDoubleOrNull() ?: return@AddReadingForm
                            val nextId = (record.readings.maxOfOrNull { it.id } ?: 0L) + 1L
                            val updated = record.readings
                                .filterNot { it.year == pickedYear && it.month == pickedMonth } +
                                MeterReading(nextId, pickedYear, pickedMonth, parsed)
                            onUpdate(record.copy(readings = updated))
                            readingDraft = ""
                        }
                    )
                }
                item {
                    SectionHeader(title = "History")
                }
                if (record.readings.isEmpty()) {
                    item {
                        EmptyHistory()
                    }
                } else {
                    items(
                        items = record.sortedReadings.reversed(),
                        key = { it.id }
                    ) { reading ->
                        ReadingRow(
                            reading = reading,
                            unit = unit,
                            onDelete = {
                                onUpdate(record.copy(readings = record.readings.filterNot { it.id == reading.id }))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsTopBar(record: UtilityRecord, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BeigeBackground)
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, BeigeBorder, CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                UtilityIcon(
                    kind = UtilityIconKind.Back,
                    tint = TextPrimary,
                    size = 18.dp,
                    strokeWidth = 2.0.dp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Utility details",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = record.kind.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryAccentMuted.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                UtilityIcon(
                    kind = record.kind.iconKind,
                    tint = PrimaryAccent,
                    size = 24.dp,
                    strokeWidth = 1.8.dp
                )
            }
        }
    }
}

@Composable
private fun UsageCard(record: UtilityRecord, unit: String) {
    val target = if (record.readings.size >= 2) 1f else 0.25f
    val anim by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(420),
        label = "usage"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = PrimaryAccent.copy(alpha = 0.12f),
                spotColor = PrimaryAccent.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Last month usage",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = formatNumber(record.lastMonthUsage),
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.graphicsLayer { this.alpha = anim }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (record.readings.size >= 2) record.lastMonthLabel
            else "Add at least two readings to compute usage",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun AddReadingForm(
    unit: String,
    pickedYear: Int,
    onYearChange: (Int) -> Unit,
    pickedMonth: Int,
    onMonthChange: (Int) -> Unit,
    readingDraft: String,
    onReadingDraftChange: (String) -> Unit,
    alreadyExists: Boolean,
    maxYear: Int,
    maxMonthInMaxYear: Int,
    onAdd: () -> Unit
) {
    val maxAllowedMonth = if (pickedYear == maxYear) maxMonthInMaxYear else 12
    val isFuturePick = pickedMonth > maxAllowedMonth
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Year",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        YearStepper(
            year = pickedYear,
            onChange = onYearChange,
            min = MIN_RECORD_YEAR,
            max = maxYear
        )
        Text(
            text = "Month",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        MonthGrid(
            picked = pickedMonth,
            onPick = onMonthChange,
            maxAllowedMonth = maxAllowedMonth
        )
        Text(
            text = "Reading value ($unit)",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        BeigeOutlinedField(
            value = readingDraft,
            onValueChange = onReadingDraftChange,
            placeholder = "e.g. 13302.5",
            keyboardType = KeyboardType.Decimal
        )
        Text(
            text = "Decimals supported — use \".\" or \",\" (e.g. 12.5).",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        if (alreadyExists && !isFuturePick) {
            Text(
                text = "A reading already exists for ${monthFullName(pickedMonth)} $pickedYear. Adding will replace it.",
                style = MaterialTheme.typography.bodySmall,
                color = WarningAccent
            )
        }
        if (isFuturePick) {
            Text(
                text = "Readings can only be added up to the current month.",
                style = MaterialTheme.typography.bodySmall,
                color = WarningAccent
            )
        }
        val canAdd = readingDraft.toDoubleOrNull() != null && !isFuturePick
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (canAdd) PrimaryAccent
                    else PrimaryAccentMuted.copy(alpha = 0.55f)
                )
                .clickable(enabled = canAdd, onClick = onAdd)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UtilityIcon(
                kind = UtilityIconKind.Plus,
                tint = if (canAdd) MaterialTheme.colorScheme.surface else TextSecondary,
                size = 16.dp,
                strokeWidth = 2.4.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add reading",
                style = MaterialTheme.typography.labelLarge,
                color = if (canAdd) MaterialTheme.colorScheme.surface else TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun YearStepper(
    year: Int,
    onChange: (Int) -> Unit,
    min: Int,
    max: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperArrow(
            label = "−",
            enabled = year > min,
            onClick = { if (year > min) onChange(year - 1) }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(PrimaryAccentMuted.copy(alpha = 0.45f))
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        StepperArrow(
            label = "+",
            enabled = year < max,
            onClick = { if (year < max) onChange(year + 1) }
        )
    }
}

@Composable
private fun StepperArrow(label: String, enabled: Boolean, onClick: () -> Unit) {
    val tint = if (enabled) PrimaryAccent else BeigeBorderStrong
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (enabled) PrimaryAccentMuted.copy(alpha = 0.55f) else BeigeBackground)
            .border(1.dp, BeigeBorder, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = tint,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MonthGrid(
    picked: Int,
    onPick: (Int) -> Unit,
    maxAllowedMonth: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (rowStart in 1..12 step 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (m in rowStart until rowStart + 4) {
                    val isPicked = m == picked
                    val isAllowed = m <= maxAllowedMonth
                    val bg = when {
                        isPicked && isAllowed -> PrimaryAccent
                        !isAllowed -> BeigeBorder.copy(alpha = 0.30f)
                        else -> PrimaryAccentMuted.copy(alpha = 0.40f)
                    }
                    val borderColor = when {
                        isPicked && isAllowed -> PrimaryAccent
                        else -> BeigeBorder
                    }
                    val labelColor = when {
                        isPicked && isAllowed -> MaterialTheme.colorScheme.surface
                        !isAllowed -> TextSecondary.copy(alpha = 0.5f)
                        else -> TextPrimary
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .border(
                                width = 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = isAllowed) { onPick(m) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = monthName(m),
                            style = MaterialTheme.typography.labelLarge,
                            color = labelColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingRow(
    reading: MeterReading,
    unit: String,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryAccentMuted.copy(alpha = 0.55f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${monthName(reading.month)} ${reading.year}",
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${formatNumber(reading.value)} $unit",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(WarningSoft.copy(alpha = 0.6f))
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            UtilityIcon(
                kind = UtilityIconKind.Trash,
                tint = WarningAccent,
                size = 18.dp,
                strokeWidth = 1.8.dp
            )
        }
    }
}

@Composable
private fun EmptyHistory() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SuccessSoft.copy(alpha = 0.5f))
            .border(1.dp, BeigeBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            UtilityIcon(
                kind = UtilityIconKind.Calendar,
                tint = SuccessAccent,
                size = 20.dp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "No readings yet — add your first reading to start tracking usage.",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
    }
}

private fun formatPlain(value: Double): String {
    if (value == value.toLong().toDouble()) return value.toLong().toString()
    return "%.4f".format(java.util.Locale.US, value).trimEnd('0').trimEnd('.')
}

private fun formatNumber(value: Double): String {
    if (value == 0.0) return "0"
    if (value == value.toLong().toDouble() && kotlin.math.abs(value) < 1e9) return value.toLong().toString()
    return "%.2f".format(java.util.Locale.US, value)
}

