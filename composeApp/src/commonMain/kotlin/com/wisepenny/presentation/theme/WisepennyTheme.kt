package com.wisepenny.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WisepennyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WisepennyDarkColorScheme,
        typography = WisepennyTypography,
        shapes = WisepennyShapes,
        content = content,
    )
}
