package com.wisepenny.presentation.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes

@OptIn(ExperimentalMaterial3Api::class)
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
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = WisepennyColors.SurfaceElevated,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextPrimary,
            )
            AmountTextField(
                text = text,
                onValueChange = { text = it },
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { cents?.let(onConfirm) },
                enabled = cents != null && cents > 0,
                shape = WisepennyShapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WisepennyColors.AccentMint,
                    contentColor = WisepennyColors.TextOnLight,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) { Text(confirmLabel, fontWeight = FontWeight.Bold) }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = WisepennyColors.AccentMint),
            ) { Text("Annuler") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = WisepennyColors.SurfaceElevated,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = "Programmer une auto-épargne",
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextPrimary,
            )
            AmountTextField(
                text = text,
                onValueChange = { text = it },
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth(),
            )
            CadenceRow(cadence = cadence, onCadenceChange = { cadence = it })
            Button(
                onClick = { cents?.let { onConfirm(it, cadence) } },
                enabled = cents != null && cents > 0,
                shape = WisepennyShapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WisepennyColors.AccentMint,
                    contentColor = WisepennyColors.TextOnLight,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Programmer", fontWeight = FontWeight.Bold) }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = WisepennyColors.AccentMint),
            ) { Text("Annuler") }
        }
    }
}
