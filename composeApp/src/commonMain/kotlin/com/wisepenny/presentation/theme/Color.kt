package com.wisepenny.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

object WisepennyColors {
    val BackgroundPrimary = Color(0xFF0A0F1A)
    val SurfaceElevated = Color(0xFF141B2A)
    val BorderSubtle = Color(0xFF1F2937)

    val AccentMint = Color(0xFF5EEAD4)
    val AccentMintPressed = Color(0xFF2DD4BF)
    val AccentMintSoft = Color(0xFFCCFBF1)

    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFE5E7EB)
    val TextTertiary = Color(0xFF9CA3AF)
    val TextDisabled = Color(0xFF6B7280)

    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceLightAlt = Color(0xFFF3F4F6)
    val TextOnLight = Color(0xFF0A0F1A)
    val TextOnLightMuted = Color(0xFF4B5563)

    val Success = Color(0xFF22C55E)
    val Danger = Color(0xFFEF4444)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)
}

val WisepennyDarkColorScheme = darkColorScheme(
    primary = WisepennyColors.AccentMint,
    onPrimary = WisepennyColors.TextOnLight,
    primaryContainer = WisepennyColors.AccentMintSoft,
    onPrimaryContainer = WisepennyColors.TextOnLight,
    secondary = WisepennyColors.AccentMint,
    onSecondary = WisepennyColors.TextOnLight,
    background = WisepennyColors.BackgroundPrimary,
    onBackground = WisepennyColors.TextPrimary,
    surface = WisepennyColors.SurfaceElevated,
    onSurface = WisepennyColors.TextPrimary,
    surfaceVariant = WisepennyColors.SurfaceElevated,
    onSurfaceVariant = WisepennyColors.TextSecondary,
    outline = WisepennyColors.BorderSubtle,
    error = WisepennyColors.Danger,
    onError = WisepennyColors.TextPrimary,
)
