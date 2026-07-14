package com.wisepenny.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler as AndroidBackHandler

/** On Android, keep the system back gesture behaviour unchanged. */
actual val swallowTabBackGesture: Boolean = false

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    AndroidBackHandler(enabled, onBack)
}
