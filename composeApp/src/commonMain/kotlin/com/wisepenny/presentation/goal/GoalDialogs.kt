package com.wisepenny.presentation.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes

/**
 * Amount-entry dialog for "Ajouter à l'objectif".
 *
 * Android: a centered [androidx.compose.material3.AlertDialog].
 * iOS: a [androidx.compose.material3.ModalBottomSheet] sliding up from the
 * bottom, matching the platform's native sheet convention.
 */
@Composable
expect fun AmountDialog(
    title: String,
    confirmLabel: String,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
)

/**
 * Amount + cadence dialog for "Programmer une auto-épargne".
 *
 * Android: a centered [androidx.compose.material3.AlertDialog].
 * iOS: a [androidx.compose.material3.ModalBottomSheet] sliding up from the
 * bottom, matching the platform's native sheet convention.
 */
@Composable
expect fun AutoSaveDialog(
    onConfirm: (Long, SavingsCadence) -> Unit,
    onDismiss: () -> Unit,
)

@Composable
internal fun AmountTextField(
    text: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
        label = { Text("Montant en €") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.focusRequester(focusRequester),
    )
}

@Composable
internal fun CadenceRow(cadence: SavingsCadence, onCadenceChange: (SavingsCadence) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        CadenceChip("Par semaine", cadence == SavingsCadence.WEEKLY) {
            onCadenceChange(SavingsCadence.WEEKLY)
        }
        CadenceChip("Par mois", cadence == SavingsCadence.MONTHLY) {
            onCadenceChange(SavingsCadence.MONTHLY)
        }
    }
}

@Composable
private fun CadenceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = WisepennyColors.AccentMint,
                contentColor = WisepennyColors.TextOnLight,
            ),
            shape = WisepennyShapes.extraSmall,
        ) { Text(label, style = MaterialTheme.typography.labelSmall) }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = WisepennyShapes.extraSmall,
        ) { Text(label, style = MaterialTheme.typography.labelSmall, color = WisepennyColors.TextSecondary) }
    }
}
