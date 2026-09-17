package com.wisepenny.presentation.profile

import androidx.compose.runtime.Composable

/**
 * Platform-appropriate presentation for a simple title/body info popup
 * (used by the Confidentialité and À propos rows on the profile screen).
 *
 * Android: a centered [androidx.compose.material3.AlertDialog].
 * iOS: a [androidx.compose.material3.ModalBottomSheet] sliding up from the
 * bottom, matching the platform's native sheet convention.
 */
@Composable
expect fun InfoOverlay(title: String, body: String, onDismiss: () -> Unit)
