package com.wisepenny.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyTheme

/**
 * The frame every top-level (tab) screen sits in. Owns background, status-bar
 * insets, outer padding, and optional scroll — previously duplicated (and
 * divergent) across screens. Content is laid out in a [Column].
 */
@Composable
fun WisepennyScaffold(
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Spacing.lg),
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WisepennyColors.BackgroundPrimary)
            .statusBarsPadding()
            .then(if (scrollable) Modifier.verticalScroll(scrollState) else Modifier)
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.xl, bottom = Spacing.xxl),
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

/**
 * Consistent screen title with optional leading (e.g. avatar) and trailing slots.
 * Title uses headlineMedium so every screen's heading matches.
 */
@Composable
fun WisepennyScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        leading?.invoke()
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}

@Preview
@Composable
private fun WisepennyScaffoldPreview() {
    WisepennyTheme {
        WisepennyScaffold {
            WisepennyScreenHeader(title = "Titre d'écran")
            Text("Contenu", color = WisepennyColors.TextPrimary)
        }
    }
}
