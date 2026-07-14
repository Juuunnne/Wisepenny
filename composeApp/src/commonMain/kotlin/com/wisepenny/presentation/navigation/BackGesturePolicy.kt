package com.wisepenny.presentation.navigation

import androidx.compose.runtime.Composable

/**
 * Whether the platform's leading-edge swipe-back gesture should be swallowed
 * while a top-level tab is showing.
 *
 * iOS: the leading-edge back swipe is always on and cannot be disabled through
 * [androidx.compose.ui.window.ComposeUIViewController] config (only the opposite
 * end edge is configurable), so we consume it in the nav host to keep tabs
 * tap-only. Detail screens keep the swipe because the bottom bar is hidden there.
 *
 * Android: false — the system back gesture is left intact, so swiping still
 * walks the back stack as before.
 */
expect val swallowTabBackGesture: Boolean

/**
 * A platform-agnostic back handler. On Android, this hooks into the system back button/gesture.
 * On iOS, this is used to consume the always-on leading-edge swipe when [enabled] is true.
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
