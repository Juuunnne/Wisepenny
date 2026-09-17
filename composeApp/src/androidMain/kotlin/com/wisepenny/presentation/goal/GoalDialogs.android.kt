package com.wisepenny.presentation.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.presentation.theme.Spacing

@Composable
actual fun AmountDialog(
    title: String,
    confirmLabel: String,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val cents = text.toLongOrNull()?.times(100)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            AmountTextField(text = text, onValueChange = { text = it }, focusRequester = focusRequester)
        },
        confirmButton = {
            TextButton(
                onClick = { cents?.let(onConfirm) },
                enabled = cents != null && cents > 0,
            ) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } },
    )
}

@Composable
actual fun AutoSaveDialog(
    onConfirm: (Long, SavingsCadence) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var cadence by remember { mutableStateOf(SavingsCadence.WEEKLY) }
    val cents = text.toLongOrNull()?.times(100)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Programmer une auto-épargne") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                AmountTextField(text = text, onValueChange = { text = it }, focusRequester = focusRequester)
                CadenceRow(cadence = cadence, onCadenceChange = { cadence = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { cents?.let { onConfirm(it, cadence) } },
                enabled = cents != null && cents > 0,
            ) { Text("Programmer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } },
    )
}
