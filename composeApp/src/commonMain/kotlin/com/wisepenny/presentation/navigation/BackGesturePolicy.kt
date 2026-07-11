package com.wisepenny.presentation.navigation

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
