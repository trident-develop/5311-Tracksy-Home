package com.bailing.lark.roll.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

@Composable
fun SummaryCard(
    dueSoon: Int,
    overdue: Int,
    paidThisMonth: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = PrimaryAccent.copy(alpha = 0.12f),
                spotColor = PrimaryAccent.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BeigeBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "This month",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Home bills overview",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            PulsingDot()
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryStat(
                count = dueSoon,
                label = "Due soon",
                accent = PrimaryAccent,
                soft = PrimaryAccentMuted,
                modifier = Modifier.weight(1f)
            )
            SummaryStat(
                count = overdue,
                label = "Overdue",
                accent = WarningAccent,
                soft = WarningSoft,
                modifier = Modifier.weight(1f)
            )
            SummaryStat(
                count = paidThisMonth,
                label = "Paid",
                accent = SuccessAccent,
                soft = SuccessSoft,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PulsingDot() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size((42 * scale).dp)
            .clip(CircleShape)
            .background(PrimaryAccent.copy(alpha = 0.10f * alpha)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(PrimaryAccentMuted),
            contentAlignment = Alignment.Center
        ) {
            UtilityIcon(
                kind = UtilityIconKind.House,
                tint = PrimaryAccent,
                size = 18.dp,
                strokeWidth = 1.6.dp
            )
        }
    }
}

@Composable
private fun SummaryStat(
    count: Int,
    label: String,
    accent: Color,
    soft: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(soft.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
    }
}
