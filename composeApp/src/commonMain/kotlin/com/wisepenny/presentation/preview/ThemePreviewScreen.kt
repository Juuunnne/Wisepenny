package com.wisepenny.presentation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
@Preview
@Composable
fun ThemePreviewScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = WisepennyColors.BackgroundPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            SectionTitle("Typography")
            Text("127,40 €", style = MaterialTheme.typography.displayLarge, color = WisepennyColors.TextPrimary)
            Text("450 €", style = MaterialTheme.typography.displayMedium, color = WisepennyColors.TextPrimary)
            Text("7 jours sans café", style = MaterialTheme.typography.headlineMedium, color = WisepennyColors.TextPrimary)
            Text("Mes objectifs", style = MaterialTheme.typography.titleMedium, color = WisepennyColors.TextPrimary)
            Text("Économise 21 € en une semaine.", style = MaterialTheme.typography.bodyMedium, color = WisepennyColors.TextSecondary)
            Text("ÉPARGNE DU MOIS", style = MaterialTheme.typography.labelSmall, color = WisepennyColors.TextTertiary)

            SectionTitle("Colors")
            ColorSwatch("BackgroundPrimary", WisepennyColors.BackgroundPrimary, textOnSwatch = WisepennyColors.TextPrimary)
            ColorSwatch("SurfaceElevated", WisepennyColors.SurfaceElevated, textOnSwatch = WisepennyColors.TextPrimary)
            ColorSwatch("BorderSubtle", WisepennyColors.BorderSubtle, textOnSwatch = WisepennyColors.TextPrimary)
            ColorSwatch("AccentMint", WisepennyColors.AccentMint, textOnSwatch = WisepennyColors.TextOnLight)
            ColorSwatch("AccentMintPressed", WisepennyColors.AccentMintPressed, textOnSwatch = WisepennyColors.TextOnLight)
            ColorSwatch("AccentMintSoft", WisepennyColors.AccentMintSoft, textOnSwatch = WisepennyColors.TextOnLight)
            ColorSwatch("SurfaceLight", WisepennyColors.SurfaceLight, textOnSwatch = WisepennyColors.TextOnLight)
            ColorSwatch("Success", WisepennyColors.Success, textOnSwatch = WisepennyColors.TextPrimary)
            ColorSwatch("Danger", WisepennyColors.Danger, textOnSwatch = WisepennyColors.TextPrimary)
            ColorSwatch("Warning", WisepennyColors.Warning, textOnSwatch = WisepennyColors.TextOnLight)
            ColorSwatch("Info", WisepennyColors.Info, textOnSwatch = WisepennyColors.TextPrimary)

            SectionTitle("Shapes (corner radius)")
            ShapeRow(12.dp, "12")
            ShapeRow(16.dp, "16")
            ShapeRow(20.dp, "20")
            ShapeRow(24.dp, "24")
            ShapeRow(28.dp, "28")

            SectionTitle("Spacing scale")
            SpacingRow("xxs", Spacing.xxs)
            SpacingRow("xs", Spacing.xs)
            SpacingRow("sm", Spacing.sm)
            SpacingRow("md", Spacing.md)
            SpacingRow("lg", Spacing.lg)
            SpacingRow("xl", Spacing.xl)
            SpacingRow("xxl", Spacing.xxl)
            SpacingRow("xxxl", Spacing.xxxl)
            SpacingRow("huge", Spacing.huge)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = WisepennyColors.AccentMint,
    )
}

@Composable
private fun ColorSwatch(name: String, swatch: Color, textOnSwatch: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(swatch, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyMedium, color = textOnSwatch)
    }
}

@Composable
private fun ShapeRow(radius: androidx.compose.ui.unit.Dp, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(WisepennyColors.AccentMint, shape = RoundedCornerShape(radius)),
        )
        Text("$label dp", style = MaterialTheme.typography.bodyMedium, color = WisepennyColors.TextPrimary)
    }
}

@Composable
private fun SpacingRow(name: String, value: androidx.compose.ui.unit.Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .width(value)
                .height(16.dp)
                .background(WisepennyColors.AccentMint),
        )
        Text("$name = $value", style = MaterialTheme.typography.bodyMedium, color = WisepennyColors.TextSecondary)
    }
}
