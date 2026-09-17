package com.wisepenny.presentation.profile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.wisepenny.presentation.theme.WisepennyColors

@Composable
actual fun InfoOverlay(title: String, body: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WisepennyColors.SurfaceElevated,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextPrimary,
            )
        },
        text = {
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextSecondary,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Fermer", color = WisepennyColors.AccentMint)
            }
        },
    )
}
