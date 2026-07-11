package com.wisepenny.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.onboarding.WelcomeContent
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyTheme

private enum class ProfileOverlay { NONE, REPLAY, RGPD, ABOUT, RESET_CONFIRM }

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onResetData: () -> Unit,
) {
    var overlay by remember { mutableStateOf(ProfileOverlay.NONE) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.xl, bottom = Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text(
                text = "Profil",
                style = MaterialTheme.typography.headlineMedium,
                color = WisepennyColors.TextPrimary,
            )

            ProfileCard(state)

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                SettingsRow("📖", "Revoir l'introduction") { overlay = ProfileOverlay.REPLAY }
                SettingsRow("🔒", "Confidentialité (RGPD)") { overlay = ProfileOverlay.RGPD }
                SettingsRow("ℹ️", "À propos") { overlay = ProfileOverlay.ABOUT }
                SettingsRow(
                    emoji = "🗑️",
                    title = "Réinitialiser mes données",
                    tint = WisepennyColors.Danger,
                ) { overlay = ProfileOverlay.RESET_CONFIRM }
            }
        }
    }

    when (overlay) {
        ProfileOverlay.NONE -> Unit
        ProfileOverlay.REPLAY -> ReplayOverlay(onClose = { overlay = ProfileOverlay.NONE })
        ProfileOverlay.RGPD -> InfoDialog(
            title = "Confidentialité (RGPD)",
            body = "Wisepenny applique la minimisation des données : seules les " +
                "informations nécessaires à ton épargne sont conservées, en local sur " +
                "ton appareil. Aucune donnée n'est partagée sans ton consentement. Tu " +
                "peux exercer ton droit à l'effacement à tout moment via " +
                "« Réinitialiser mes données ».",
            onDismiss = { overlay = ProfileOverlay.NONE },
        )
        ProfileOverlay.ABOUT -> InfoDialog(
            title = "À propos",
            body = "Wisepenny — épargne et éducation financière pour les 18–26 ans.\n\n" +
                "Version 0.8 (MVP)\nProjet capstone RNCP39583.",
            onDismiss = { overlay = ProfileOverlay.NONE },
        )
        ProfileOverlay.RESET_CONFIRM -> ResetConfirmDialog(
            onConfirm = {
                overlay = ProfileOverlay.NONE
                onResetData()
            },
            onDismiss = { overlay = ProfileOverlay.NONE },
        )
    }
}

@Composable
private fun ProfileCard(state: ProfileUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(WisepennyColors.SurfaceElevated)
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = "Ma motivation",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextTertiary,
        )
        Text(
            text = state.motivation.ifBlank { "—" },
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.TextPrimary,
        )
        if (state.memberSinceLabel.isNotBlank()) {
            Text(
                text = "Membre depuis le ${state.memberSinceLabel}",
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextTertiary,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
private fun SettingsRow(
    emoji: String,
    title: String,
    tint: androidx.compose.ui.graphics.Color = WisepennyColors.TextPrimary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WisepennyColors.SurfaceElevated)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = emoji, style = MaterialTheme.typography.titleMedium)
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = tint,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReplayOverlay(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WisepennyColors.BackgroundPrimary)
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.huge, bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            WelcomeContent()
        }
        Button(
            onClick = onClose,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WisepennyColors.AccentMint,
                contentColor = WisepennyColors.TextOnLight,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Fermer", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun InfoDialog(title: String, body: String, onDismiss: () -> Unit) {
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

@Composable
private fun ResetConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WisepennyColors.SurfaceElevated,
        title = {
            Text(
                text = "Réinitialiser mes données ?",
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextPrimary,
            )
        },
        text = {
            Text(
                text = "Tes objectifs, défis et progrès seront définitivement effacés " +
                    "et l'introduction recommencera. Cette action est irréversible.",
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextSecondary,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Effacer", color = WisepennyColors.Danger, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Annuler", color = WisepennyColors.TextTertiary)
            }
        },
    )
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    WisepennyTheme {
        ProfileScreen(
            state = ProfileUiState(
                motivation = "Réaliser le voyage de mes rêves",
                memberSinceLabel = "8 juillet 2026",
            ),
            onResetData = {},
        )
    }
}
