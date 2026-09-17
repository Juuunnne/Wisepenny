package com.wisepenny.presentation.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.components.WisepennyCard
import com.wisepenny.presentation.components.WisepennyCardVariant
import com.wisepenny.presentation.components.WisepennyTopBar
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes
import com.wisepenny.presentation.theme.WisepennyTheme

data class LinkChallengeUiState(
    val challenges: List<LinkableChallengeItem> = emptyList(),
)

data class LinkableChallengeItem(
    val id: Long,
    val iconKey: String,
    val title: String,
    val subtitle: String,
    val amountLabel: String,
)

@Composable
fun LinkChallengeScreen(
    state: LinkChallengeUiState,
    onBack: () -> Unit,
    onLinkChallenge: (Long) -> Unit,
) {
    Scaffold(
        topBar = { WisepennyTopBar(title = "Lier un défi", onBack = onBack) },
        containerColor = WisepennyColors.BackgroundPrimary,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.lg, bottom = Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            if (state.challenges.isEmpty()) {
                Text(
                    text = "Aucun défi disponible pour l'instant. " +
                        "Accepte le défi du jour depuis le tableau de bord pour en créer un.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextTertiary,
                )
            } else {
                state.challenges.forEach { challenge ->
                    LinkableChallengeCard(
                        challenge = challenge,
                        onClick = { onLinkChallenge(challenge.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LinkableChallengeCard(challenge: LinkableChallengeItem, onClick: () -> Unit) {
    WisepennyCard(
        variant = WisepennyCardVariant.Elevated,
        shape = WisepennyShapes.medium,
        onClick = onClick,
        contentPadding = PaddingValues(Spacing.md),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(WisepennyColors.BackgroundPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = goalEmoji(challenge.iconKey), style = MaterialTheme.typography.bodyMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = challenge.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextTertiary,
                )
            }
            Text(
                text = challenge.amountLabel,
                style = MaterialTheme.typography.labelSmall,
                color = WisepennyColors.AccentMint,
                fontWeight = FontWeight.Bold,
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Lier ce défi",
                tint = WisepennyColors.AccentMint,
            )
        }
    }
}

@Preview
@Composable
private fun LinkChallengeScreenPreview() {
    WisepennyTheme {
        LinkChallengeScreen(
            state = LinkChallengeUiState(
                challenges = listOf(
                    LinkableChallengeItem(1, "coffee", "Économise le prix d'un café", "Mets 3 € de côté chaque jour", "+3 € / jour"),
                    LinkableChallengeItem(2, "shopping", "Pas d'achat impulsif", "Résiste à la tentation", "+5 € / jour"),
                ),
            ),
            onBack = {},
            onLinkChallenge = {},
        )
    }
}
