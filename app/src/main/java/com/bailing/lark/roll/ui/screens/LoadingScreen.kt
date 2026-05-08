package com.bailing.lark.roll.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bailing.lark.roll.ui.icons.UtilityIcon
import com.bailing.lark.roll.ui.icons.UtilityIconKind
import com.bailing.lark.roll.ui.theme.BeigeBackground
import com.bailing.lark.roll.ui.theme.BeigeBorder
import com.bailing.lark.roll.ui.theme.BeigeCard
import com.bailing.lark.roll.ui.theme.PrimaryAccent
import com.bailing.lark.roll.ui.theme.PrimaryAccentMuted
import com.bailing.lark.roll.ui.theme.PrimaryAccentSoft
import com.bailing.lark.roll.ui.theme.SuccessAccent
import com.bailing.lark.roll.ui.theme.SuccessSoft
import com.bailing.lark.roll.ui.theme.TextPrimary
import com.bailing.lark.roll.ui.theme.TextSecondary
import com.bailing.lark.roll.ui.theme.WarningAccent
import com.bailing.lark.roll.ui.theme.WarningSoft
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BeigeBackground,
                        BeigeBackground,
                        PrimaryAccentMuted.copy(alpha = 0.40f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        FloatingIconsBackdrop()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            HouseEmblem()
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Track your home utilities in one calm place",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))
            InfiniteProgressTrack()
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Preparing your overview…",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun HouseEmblem() {
    val transition = rememberInfiniteTransition(label = "emblem")
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring"
    )
    Box(
        modifier = Modifier
            .size(180.dp)
            .graphicsLayer { scaleX = pulse; scaleY = pulse },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(PrimaryAccentMuted.copy(alpha = ringAlpha))
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(PrimaryAccentMuted.copy(alpha = ringAlpha + 0.10f))
        )
        Box(
            modifier = Modifier
                .size(108.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = PrimaryAccent.copy(alpha = 0.22f),
                    spotColor = PrimaryAccent.copy(alpha = 0.22f)
                )
                .clip(CircleShape)
                .background(BeigeCard)
                .border(1.dp, BeigeBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            UtilityIcon(
                kind = UtilityIconKind.House,
                tint = PrimaryAccent,
                size = 56.dp,
                strokeWidth = 2.4.dp
            )
        }
    }
}

@Composable
private fun InfiniteProgressTrack() {
    val transition = rememberInfiniteTransition(label = "progress")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(6.dp)
            .clip(CircleShape)
            .background(PrimaryAccentMuted.copy(alpha = 0.45f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.4f)
                .graphicsLayer {
                    translationX = (progress * size.width * 1.8f) - (size.width * 0.4f)
                }
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            PrimaryAccentSoft.copy(alpha = 0.0f),
                            PrimaryAccent,
                            PrimaryAccentSoft.copy(alpha = 0.0f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun FloatingIconsBackdrop() {
    val items = listOf(
        FloatingDef(UtilityIconKind.Electricity, PrimaryAccent, PrimaryAccentMuted, xFraction = 0.12f, yFraction = 0.18f, sizeDp = 44, delayMs = 0, durationMs = 2600),
        FloatingDef(UtilityIconKind.Water, PrimaryAccentSoft, PrimaryAccentMuted, xFraction = 0.80f, yFraction = 0.22f, sizeDp = 38, delayMs = 400, durationMs = 2800),
        FloatingDef(UtilityIconKind.Gas, WarningAccent, WarningSoft, xFraction = 0.18f, yFraction = 0.78f, sizeDp = 36, delayMs = 800, durationMs = 2400),
        FloatingDef(UtilityIconKind.Bell, PrimaryAccent, PrimaryAccentMuted, xFraction = 0.82f, yFraction = 0.74f, sizeDp = 40, delayMs = 1200, durationMs = 3000),
        FloatingDef(UtilityIconKind.Calendar, SuccessAccent, SuccessSoft, xFraction = 0.08f, yFraction = 0.50f, sizeDp = 34, delayMs = 600, durationMs = 2700),
        FloatingDef(UtilityIconKind.Internet, PrimaryAccentSoft, PrimaryAccentMuted, xFraction = 0.88f, yFraction = 0.50f, sizeDp = 34, delayMs = 1000, durationMs = 2500),
    )
    androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight
        items.forEach { def ->
            FloatingIcon(def = def, parentW = w, parentH = h)
        }
    }
}

private data class FloatingDef(
    val kind: UtilityIconKind,
    val accent: androidx.compose.ui.graphics.Color,
    val soft: androidx.compose.ui.graphics.Color,
    val xFraction: Float,
    val yFraction: Float,
    val sizeDp: Int,
    val delayMs: Int,
    val durationMs: Int
)

@Composable
private fun FloatingIcon(
    def: FloatingDef,
    parentW: androidx.compose.ui.unit.Dp,
    parentH: androidx.compose.ui.unit.Dp
) {
    val transition = rememberInfiniteTransition(label = "floating-${def.kind.name}")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(def.durationMs, delayMillis = def.delayMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translate"
    )
    val alpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(def.durationMs, delayMillis = def.delayMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val rotate by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(def.durationMs, delayMillis = def.delayMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )
    val cx = parentW * def.xFraction
    val cy = parentH * def.yFraction
    val xOffset = cx - (def.sizeDp.dp / 2)
    val yOffset = cy - (def.sizeDp.dp / 2) - translate.dp
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(def.sizeDp.dp)
            .graphicsLayer {
                rotationZ = rotate
                this.alpha = alpha
            }
            .clip(RoundedCornerShape(14.dp))
            .background(def.soft.copy(alpha = 0.35f))
            .border(1.dp, BeigeBorder, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        UtilityIcon(
            kind = def.kind,
            tint = def.accent,
            size = (def.sizeDp - 16).dp,
            strokeWidth = 1.8.dp
        )
    }
}