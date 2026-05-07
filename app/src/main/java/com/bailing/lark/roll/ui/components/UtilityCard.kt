package com.bailing.lark.roll.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bailing.lark.roll.data.BillStatus
import com.bailing.lark.roll.data.UtilityBill
import com.bailing.lark.roll.data.UtilityRecord
import com.bailing.lark.roll.data.monthName
import com.bailing.lark.roll.ui.icons.UtilityIcon
import com.bailing.lark.roll.ui.icons.UtilityIconKind
import com.bailing.lark.roll.ui.theme.BeigeBorder
import com.bailing.lark.roll.ui.theme.PrimaryAccent
import com.bailing.lark.roll.ui.theme.PrimaryAccentMuted
import com.bailing.lark.roll.ui.theme.SuccessAccent
import com.bailing.lark.roll.ui.theme.SuccessSoft
import com.bailing.lark.roll.ui.theme.TextPrimary
import com.bailing.lark.roll.ui.theme.TextSecondary
import com.bailing.lark.roll.ui.theme.WarningAccent
import com.bailing.lark.roll.ui.theme.WarningSoft

private data class StatusVisuals(
    val label: String,
    val accent: Color,
    val soft: Color
)

@Composable
private fun BillStatus.visuals(): StatusVisuals = when (this) {
    BillStatus.Paid -> StatusVisuals("Paid", SuccessAccent, SuccessSoft)
    BillStatus.DueSoon -> StatusVisuals("Due soon", PrimaryAccent, PrimaryAccentMuted)
    BillStatus.Overdue -> StatusVisuals("Overdue", WarningAccent, WarningSoft)
}

@Composable
fun UtilityCard(
    bill: UtilityBill,
    record: UtilityRecord?,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onMarkPaid: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visuals = bill.status.visuals()
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(280),
        label = "chevron"
    )

    val providerDisplay = record?.providerName ?: bill.provider
    val unit = bill.kind.unit.ifEmpty { "units" }
    val ready = record != null && record.readings.size >= 2
    val computedAmount = if (ready) "$ %.2f".format(java.util.Locale.US, record!!.lastMonthCost) else null
    val computedUsage = if (ready) {
        val last = record!!.sortedReadings.last()
        "Used %s %s in %s %d".format(
            java.util.Locale.US,
            formatUsage(record.lastMonthUsage),
            unit,
            monthName(last.month),
            last.year
        )
    } else null
    val amountDisplay = computedAmount ?: bill.amount
    val usageDisplay = computedUsage ?: bill.usageNote
    val tariffDisplay = record?.let {
        "$ %.2f / %s".format(java.util.Locale.US, it.tariff, unit)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = PrimaryAccent.copy(alpha = 0.10f),
                spotColor = PrimaryAccent.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onToggleExpanded)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UtilityIconBadge(
                kind = bill.kind.iconKind,
                accent = visuals.accent,
                soft = visuals.soft,
                animate = bill.status != BillStatus.Paid
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bill.kind.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = bill.dueDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amountDisplay,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                StatusPill(visuals = visuals, status = bill.status)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.rotate(chevronRotation)) {
                UtilityIcon(
                    kind = UtilityIconKind.Chevron,
                    tint = TextSecondary,
                    size = 16.dp,
                    strokeWidth = 1.6.dp
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(200)) + expandVertically(tween(280)),
            exit = fadeOut(tween(160)) + shrinkVertically(tween(220))
        ) {
            Column(modifier = Modifier.padding(top = 14.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BeigeBorder)
                )
                Spacer(modifier = Modifier.height(14.dp))
                DetailRow(label = "Provider", value = providerDisplay)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "Period usage", value = usageDisplay)
                Spacer(modifier = Modifier.height(8.dp))
                if (tariffDisplay != null) {
                    DetailRow(label = "Tariff", value = tariffDisplay)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                DetailRow(label = "Amount", value = "$amountDisplay (informational)")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SoftActionButton(
                        text = "View details",
                        icon = UtilityIconKind.Eye,
                        accent = PrimaryAccent,
                        soft = PrimaryAccentMuted,
                        onClick = onViewDetails,
                        modifier = Modifier.weight(1f)
                    )
                    val paid = bill.status == BillStatus.Paid
                    SoftActionButton(
                        text = if (paid) "Already paid" else "Mark as paid",
                        icon = UtilityIconKind.Check,
                        accent = if (paid) TextSecondary else SuccessAccent,
                        soft = if (paid) BeigeBorder else SuccessSoft,
                        onClick = { if (!paid) onMarkPaid() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun UtilityIconBadge(
    kind: UtilityIconKind,
    accent: Color,
    soft: Color,
    animate: Boolean
) {
    val transition = rememberInfiniteTransition(label = "badge")
    val pulse by transition.animateFloat(
        initialValue = if (animate) 0.0f else 0f,
        targetValue = if (animate) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(soft.copy(alpha = 0.55f + 0.20f * pulse)),
        contentAlignment = Alignment.Center
    ) {
        UtilityIcon(
            kind = kind,
            tint = accent,
            size = 24.dp,
            strokeWidth = 1.8.dp
        )
    }
}

@Composable
private fun StatusPill(visuals: StatusVisuals, status: BillStatus) {
    val transition = rememberInfiniteTransition(label = "pill")
    val a by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val animated = status != BillStatus.Paid
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(visuals.soft.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(visuals.accent.copy(alpha = if (animated) a else 1f))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = visuals.label,
            style = MaterialTheme.typography.labelSmall,
            color = visuals.accent,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SoftActionButton(
    text: String,
    icon: UtilityIconKind,
    accent: Color,
    soft: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(soft.copy(alpha = 0.55f))
            .border(1.dp, soft, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    ) {
        UtilityIcon(
            kind = icon,
            tint = accent,
            size = 16.dp,
            strokeWidth = 1.8.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatUsage(value: Double): String {
    if (value == 0.0) return "0"
    if (value == value.toLong().toDouble() && kotlin.math.abs(value) < 1e9) return value.toLong().toString()
    return "%.2f".format(java.util.Locale.US, value)
}
