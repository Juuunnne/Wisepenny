package com.wisepenny.presentation.learning

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.components.SavingsProgressBar
import com.wisepenny.presentation.components.WisepennyCard
import com.wisepenny.presentation.components.WisepennyCardVariant
import com.wisepenny.presentation.components.WisepennyScaffold
import com.wisepenny.presentation.components.WisepennyScreenHeader
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyTheme

enum class ModuleStatus { ACTIVE, COMPLETED, LOCKED }

data class LearningListUiState(
    val modules: List<ModuleListItem>,
)

data class ModuleListItem(
    val id: String,
    val title: String,
    val description: String,
    val levelLabel: String,
    val status: ModuleStatus,
    val progress: Float,
    val progressPercent: Int,
)

@Composable
fun LearningListScreen(
    state: LearningListUiState,
    onModuleClick: (String) -> Unit,
) {
    WisepennyScaffold {
        WisepennyScreenHeader(title = "Apprendre")
        state.modules.forEach { module ->
            ModuleCard(module = module, onClick = { onModuleClick(module.id) })
        }
    }
}

@Composable
private fun ModuleCard(module: ModuleListItem, onClick: () -> Unit) {
    val active = module.status == ModuleStatus.ACTIVE
    val locked = module.status == ModuleStatus.LOCKED

    val onCard = when {
        active -> WisepennyColors.TextOnLight
        locked -> WisepennyColors.TextDisabled
        else -> WisepennyColors.TextPrimary
    }
    val mutedOnCard = when {
        active -> WisepennyColors.TextOnLightMuted
        locked -> WisepennyColors.TextDisabled
        else -> WisepennyColors.TextTertiary
    }

    WisepennyCard(
        variant = if (active) WisepennyCardVariant.Light else WisepennyCardVariant.Elevated,
        onClick = if (locked) null else onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LevelPill(label = module.levelLabel, onLightCard = active)
            Spacer(Modifier.weight(1f))
            StatusIcon(status = module.status)
        }
        Text(
            text = module.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = onCard,
        )
        Text(
            text = module.description,
            style = MaterialTheme.typography.bodyMedium,
            color = mutedOnCard,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        when (module.status) {
            ModuleStatus.ACTIVE -> Column(
                modifier = Modifier.padding(top = Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Progression",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedOnCard,
                    )
                    Text(
                        text = "${module.progressPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = WisepennyColors.AccentMintPressed,
                        fontWeight = FontWeight.Bold,
                    )
                }
                SavingsProgressBar(
                    progress = module.progress,
                    trackColor = WisepennyColors.SurfaceLightAlt,
                )
            }

            ModuleStatus.COMPLETED -> Text(
                text = "Terminé",
                style = MaterialTheme.typography.labelSmall,
                color = WisepennyColors.AccentMint,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Spacing.xxs),
            )

            ModuleStatus.LOCKED -> Unit
        }
    }
}

@Composable
private fun LevelPill(label: String, onLightCard: Boolean) {
    val color = if (onLightCard) WisepennyColors.TextOnLightMuted else WisepennyColors.TextTertiary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, color, RoundedCornerShape(50))
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
private fun StatusIcon(status: ModuleStatus) {
    when (status) {
        ModuleStatus.ACTIVE -> Icon(
            imageVector = Icons.Filled.PlayCircle,
            contentDescription = "Commencer",
            tint = WisepennyColors.TextOnLight,
            modifier = Modifier.size(28.dp),
        )

        ModuleStatus.COMPLETED -> Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Terminé",
            tint = WisepennyColors.AccentMint,
            modifier = Modifier.size(28.dp),
        )

        ModuleStatus.LOCKED -> Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = "Verrouillé",
            tint = WisepennyColors.TextTertiary,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview
@Composable
private fun LearningListScreenPreview() {
    WisepennyTheme {
        LearningListScreen(
            state = LearningListUiState(
                modules = listOf(
                    ModuleListItem(
                        id = "bourse",
                        title = "Les bases de la bourse",
                        description = "Comprendre les fondations des marchés pour investir avec confiance.",
                        levelLabel = "Débutant",
                        status = ModuleStatus.ACTIVE,
                        progress = 0.4f,
                        progressPercent = 40,
                    ),
                    ModuleListItem(
                        id = "epargne-auto",
                        title = "Épargner sans effort",
                        description = "Les stratégies d'automatisation pour construire un capital sans y penser.",
                        levelLabel = "Débutant",
                        status = ModuleStatus.LOCKED,
                        progress = 0f,
                        progressPercent = 0,
                    ),
                    ModuleListItem(
                        id = "budget",
                        title = "Ton premier budget",
                        description = "Structurer ses finances personnelles pour allouer efficacement ses revenus.",
                        levelLabel = "Intermédiaire",
                        status = ModuleStatus.LOCKED,
                        progress = 0f,
                        progressPercent = 0,
                    ),
                ),
            ),
            onModuleClick = {},
        )
    }
}
