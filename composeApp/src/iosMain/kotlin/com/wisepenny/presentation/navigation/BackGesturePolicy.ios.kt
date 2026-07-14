package com.wisepenny.presentation.navigation

import androidx.compose.runtime.Composable

/** On iOS, swallow the always-on leading-edge back swipe while on a tab. */
actual val swallowTabBackGesture: Boolean = true

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have a hardware back button. 
    // The leading-edge swipe gesture can be intercepted here if needed,
    // but a common implementation is a no-op that just satisfies the compiler
    // when we're only interested in the Android back button behavior.
}
