package com.wisepenny.presentation.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.components.SavingsProgressBar
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyTheme

data class ModuleReaderUiState(
    val moduleId: String,
    val title: String,
    val pages: List<ModuleReaderPage>,
    val startIndex: Int,
)

data class ModuleReaderPage(
    val heading: String,
    val body: String,
    val example: String?,
)

/**
 * Page-by-page reader (Brilliant.org-style). One concept per screen; "Continuer"
 * persists progress and advances, "Terminer" on the last page persists completion
 * and closes. The current page is local state seeded from [ModuleReaderUiState.startIndex],
 * so progress emissions never yank the reader off the page the user is on.
 */
@Composable
fun ModuleReaderScreen(
    uiState: ModuleReaderUiState,
    onAdvance: (pageIndex: Int) -> Unit,
    onClose: () -> Unit,
) {
    if (uiState.pages.isEmpty()) return

    var currentPage by rememberSaveable(uiState.moduleId) { mutableStateOf(uiState.startIndex) }
    val pageIndex = currentPage.coerceIn(0, uiState.pages.lastIndex)
    val page = uiState.pages[pageIndex]
    val isLast = pageIndex >= uiState.pages.lastIndex

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.lg, bottom = Spacing.xl),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                SavingsProgressBar(
                    progress = (pageIndex + 1).toFloat() / uiState.pages.size,
                    modifier = Modifier.weight(1f),
                    trackColor = WisepennyColors.BorderSubtle,
                )
                Text(
                    text = "${pageIndex + 1}/${uiState.pages.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextTertiary,
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Fermer",
                        tint = WisepennyColors.TextTertiary,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = page.heading,
                    style = MaterialTheme.typography.headlineMedium,
                    color = WisepennyColors.TextPrimary,
                )
                Text(
                    text = page.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextSecondary,
                )
                page.example?.let { ExampleCallout(it) }
            }

            Button(
                onClick = {
                    onAdvance(pageIndex)
                    if (isLast) onClose() else currentPage = pageIndex + 1
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WisepennyColors.AccentMint,
                    contentColor = WisepennyColors.TextOnLight,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md)
                    .height(52.dp),
            ) {
                Text(
                    text = if (isLast) "Terminer" else "Continuer",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun ExampleCallout(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WisepennyColors.SurfaceElevated)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = "💡 Exemple",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.AccentMint,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextSecondary,
        )
    }
}

@Preview
@Composable
private fun ModuleReaderScreenPreview() {
    WisepennyTheme {
        ModuleReaderScreen(
            uiState = ModuleReaderUiState(
                moduleId = "bourse",
                title = "Les bases de la bourse",
                pages = listOf(
                    ModuleReaderPage(
                        heading = "Qu'est-ce qu'une action ?",
                        body = "Une action, c'est une petite part de propriété d'une entreprise. " +
                            "En acheter une, c'est devenir copropriétaire de la société.",
                        example = "Imagine une pizzeria découpée en 1 000 parts. Acheter une action, " +
                            "c'est posséder une de ces parts.",
                    ),
                ),
                startIndex = 0,
            ),
            onAdvance = {},
            onClose = {},
        )
    }
}
