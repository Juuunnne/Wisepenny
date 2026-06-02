package com.wisepenny.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.theme.WisepennyColors

/**
 * A thin saved-vs-target bar (DesignSystem §9.9). Track + mint fill.
 * [trackColor] is parameterised because the bar sits on both dark surfaces
 * and white hero cards, which need different tracks.
 */
@Composable
fun SavingsProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = WisepennyColors.BorderSubtle,
    fillColor: Color = WisepennyColors.AccentMint,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(fillColor),
        )
    }
}
