package com.wisepenny.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wisepenny.presentation.components.WisepennyCard
import com.wisepenny.presentation.components.WisepennyScaffold
import com.wisepenny.presentation.components.WisepennyScreenHeader
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes
import com.wisepenny.presentation.theme.WisepennyTheme

private enum class ProfileOverlay { NONE, CURRENCY, RGPD, ABOUT, RESET_CONFIRM }

/** Currencies offered in the picker — mirrors the onboarding wizard's set. */
private val PROFILE_CURRENCIES = listOf("EUR", "USD", "GBP")

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onCurrencyChange: (String) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onResetData: () -> Unit,
) {
    var overlay by remember { mutableStateOf(ProfileOverlay.NONE) }

    WisepennyScaffold {
        WisepennyScreenHeader(title = "Profil")

        IdentityCard(state)

        SettingsSection(label = "Préférences") {
            SettingsRow(
                emoji = "💶",
                title = "Devise",
                subtitle = "${currencyName(state.currency)} · ${currencySymbol(state.currency)}",
                onClick = { overlay = ProfileOverlay.CURRENCY },
                trailing = { Chevron() },
            )
            RowDivider()
            SettingsRow(
                emoji = "🔔",
                title = "Rappels quotidiens",
                subtitle = "Chaque jour, à 19 h",
                trailing = {
                    Switch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = onNotificationsToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WisepennyColors.TextOnLight,
                            checkedTrackColor = WisepennyColors.AccentMint,
                            uncheckedThumbColor = WisepennyColors.TextTertiary,
                            uncheckedTrackColor = WisepennyColors.SurfaceElevated,
                            uncheckedBorderColor = WisepennyColors.BorderSubtle,
                        ),
                    )
                },
            )
        }

        SettingsSection(label = "Aide & informations") {
            SettingsRow(
                emoji = "🔒",
                title = "Confidentialité",
                subtitle = "Conforme RGPD · tes données restent locales",
                onClick = { overlay = ProfileOverlay.RGPD },
                trailing = { Chevron() },
            )
            RowDivider()
            SettingsRow(
                emoji = "ℹ️",
                title = "À propos",
                subtitle = "Wisepenny · 0.8 · démo",
                onClick = { overlay = ProfileOverlay.ABOUT },
                trailing = { Chevron() },
            )
            RowDivider()
            SettingsRow(
                emoji = "🗑️",
                title = "Réinitialiser mes données",
                subtitle = "Effacer et recommencer l'introduction",
                tint = WisepennyColors.Danger,
                onClick = { overlay = ProfileOverlay.RESET_CONFIRM },
                trailing = { Chevron() },
            )
        }
    }

    when (overlay) {
        ProfileOverlay.NONE -> Unit
        ProfileOverlay.CURRENCY -> CurrencyPickerDialog(
            selected = state.currency,
            onSelect = {
                onCurrencyChange(it)
                overlay = ProfileOverlay.NONE
            },
            onDismiss = { overlay = ProfileOverlay.NONE },
        )
        ProfileOverlay.RGPD -> InfoOverlay(
            title = "Confidentialité (RGPD)",
            body = "Wisepenny applique la minimisation des données : seules les " +
                "informations nécessaires à ton épargne sont conservées, en local sur " +
                "ton appareil. Aucune donnée n'est partagée sans ton consentement. Tu " +
                "peux exercer ton droit à l'effacement à tout moment via " +
                "« Réinitialiser mes données ».",
            onDismiss = { overlay = ProfileOverlay.NONE },
        )
        ProfileOverlay.ABOUT -> InfoOverlay(
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
private fun IdentityCard(state: ProfileUiState) {
    WisepennyCard(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(state.firstName)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = state.firstName.ifBlank { "—" },
                    style = MaterialTheme.typography.titleMedium,
                    color = WisepennyColors.TextPrimary,
                )
                if (state.memberSinceLabel.isNotBlank()) {
                    Text(
                        text = "Membre depuis le ${state.memberSinceLabel}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WisepennyColors.TextTertiary,
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            if (state.motivation.isNotBlank()) {
                Chip(
                    text = state.motivation,
                    textColor = WisepennyColors.AccentMint,
                )
            }
            Chip(
                text = "${currencySymbol(state.currency)} · ${state.currency}",
                textColor = WisepennyColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun Avatar(firstName: String) {
    val initial = firstName.trim().firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(WisepennyColors.AccentMint),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextOnLight,
        )
    }
}

@Composable
private fun Chip(text: String, textColor: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = Modifier
            .clip(WisepennyShapes.extraLarge)
            .background(WisepennyColors.BackgroundPrimary)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
    )
}

@Composable
private fun SettingsSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextTertiary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = Spacing.xs),
        )
        WisepennyCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    emoji: String,
    title: String,
    subtitle: String? = null,
    tint: Color = WisepennyColors.TextPrimary,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTile(emoji = emoji, tint = tint)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = tint,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextTertiary,
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
private fun IconTile(emoji: String, tint: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(WisepennyShapes.extraSmall)
            .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Chevron() {
    Text(
        text = "›",
        fontSize = 24.sp,
        color = WisepennyColors.TextTertiary,
    )
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = Spacing.md + 40.dp + Spacing.md),
        color = WisepennyColors.BorderSubtle,
    )
}

@Composable
private fun CurrencyPickerDialog(
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WisepennyColors.SurfaceElevated,
        title = {
            Text(
                text = "Choisir la devise",
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextPrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                PROFILE_CURRENCIES.forEach { code ->
                    val isSelected = code == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(WisepennyShapes.small)
                            .background(
                                if (isSelected) WisepennyColors.AccentMint.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .clickable { onSelect(code) }
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${currencySymbol(code)}  ${currencyName(code)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WisepennyColors.TextPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        if (isSelected) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WisepennyColors.AccentMint,
                            )
                        }
                    }
                }
            }
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

private fun currencySymbol(code: String): String = when (code) {
    "USD" -> "$"
    "GBP" -> "£"
    else -> "€"
}

private fun currencyName(code: String): String = when (code) {
    "USD" -> "Dollar"
    "GBP" -> "Livre"
    else -> "Euro"
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    WisepennyTheme {
        ProfileScreen(
            state = ProfileUiState(
                firstName = "Léa",
                motivation = "Réaliser un projet",
                memberSinceLabel = "23 juillet 2026",
                currency = "EUR",
                notificationsEnabled = true,
            ),
            onCurrencyChange = {},
            onNotificationsToggle = {},
            onResetData = {},
        )
    }
}
