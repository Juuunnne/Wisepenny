package com.wisepenny.presentation.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * Motion tokens from DesignSystem.md §7. No bounce, no spring overshoot:
 * the brand is confident, not playful-bouncy.
 */
object Motion {
    /** 150ms — press feedback, micro-state changes. */
    const val fastMillis = 150

    /** 250ms — default transition: card expansion, modal open. */
    const val baseMillis = 250

    /** 400ms — hero number count-up, large layout transitions. */
    const val slowMillis = 400

    /** Fast-out / slow-in. Use everywhere unless an interaction demands otherwise. */
    val defaultEasing: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}
