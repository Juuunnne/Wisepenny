package com.wisepenny.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes
import com.wisepenny.presentation.theme.WisepennyTheme

enum class WisepennyCardVariant { Elevated, Light, Accent }

/**
 * The single card surface for the app. Replaces the previous mix of Material3
 * [androidx.compose.material3.Card] and raw `Column().clip().background()`.
 * Radii come from [WisepennyShapes]; colors from [WisepennyColors].
 */
@Composable
fun WisepennyCard(
    modifier: Modifier = Modifier,
    variant: WisepennyCardVariant = WisepennyCardVariant.Elevated,
    shape: Shape = WisepennyShapes.large,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(Spacing.xl),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Spacing.sm),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val container = when (variant) {
        WisepennyCardVariant.Elevated -> WisepennyColors.SurfaceElevated
        WisepennyCardVariant.Light -> WisepennyColors.SurfaceLight
        WisepennyCardVariant.Accent -> WisepennyColors.AccentMint
    }
    Column(
        modifier = modifier
            .clip(shape)
            .background(container)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(contentPadding),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

@Preview
@Composable
private fun WisepennyCardPreview() {
    WisepennyTheme {
        Column(
            modifier = Modifier.padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            WisepennyCard(variant = WisepennyCardVariant.Elevated) {
                Text("Elevated", color = WisepennyColors.TextPrimary)
            }
            WisepennyCard(variant = WisepennyCardVariant.Light) {
                Text("Light", color = WisepennyColors.TextOnLight)
            }
            WisepennyCard(variant = WisepennyCardVariant.Accent) {
                Text("Accent", color = WisepennyColors.TextOnLight)
            }
        }
    }
}
