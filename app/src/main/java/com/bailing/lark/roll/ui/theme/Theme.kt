package com.bailing.lark.roll.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BeigeColorScheme = lightColorScheme(
    primary = PrimaryAccent,
    onPrimary = BeigeCard,
    primaryContainer = PrimaryAccentMuted,
    onPrimaryContainer = TextPrimary,
    secondary = PrimaryAccentSoft,
    onSecondary = BeigeCard,
    secondaryContainer = InfoSoft,
    onSecondaryContainer = TextPrimary,
    tertiary = SuccessAccent,
    onTertiary = BeigeCard,
    tertiaryContainer = SuccessSoft,
    onTertiaryContainer = TextPrimary,
    error = WarningAccent,
    onError = BeigeCard,
    errorContainer = WarningSoft,
    onErrorContainer = TextPrimary,
    background = BeigeBackground,
    onBackground = TextPrimary,
    surface = BeigeCard,
    onSurface = TextPrimary,
    surfaceVariant = InfoSoft,
    onSurfaceVariant = TextSecondary,
    outline = BeigeBorder,
    outlineVariant = BeigeBorderStrong
)

@Composable
fun TracksyHomeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BeigeColorScheme,
        typography = AppTypography,
        content = content
    )
}
