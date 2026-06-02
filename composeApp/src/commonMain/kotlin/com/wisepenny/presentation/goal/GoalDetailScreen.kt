package com.wisepenny.presentation.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.presentation.components.SavingsProgressBar
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyTheme
import kotlin.math.roundToInt

data class GoalDetailUiState(
    val id: Long,
    val category: String,
    val name: String,
    val subtitle: String,
    val savedLabel: String,
    val targetLabel: String,
    val progress: Float,
    val percentLabel: String,
    val remainingLabel: String,
    val projectionMessage: String,
    val projectionDate: String?,
    val projectionBars: List<Float>,
    val linkedChallenges: List<LinkedChallengeItem>,
    val quickAddLabel: String,
)

data class LinkedChallengeItem(
    val id: Long,
    val iconKey: String,
    val title: String,
    val contributionLabel: String,
    val statusLabel: String?,
    val isStarted: Boolean,
)

@Composable
fun GoalDetailScreen(
    state: GoalDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onQuickAdd: () -> Unit,
    onAddManual: (Long) -> Unit,
    onSetAutoSave: (Long, SavingsCadence) -> Unit,
    onChallengeClick: (Long) -> Unit,
    onSeeAllChallenges: () -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showAutoSaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { GoalDetailTopBar(onBack = onBack, onEdit = onEdit) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            GoalHeader(category = state.category, name = state.name, subtitle = state.subtitle)
            GoalHeroCard(state = state)
            ActionCardsRow(
                onAdd = { showAddDialog = true },
                onProgram = { showAutoSaveDialog = true },
            )
            ProjectionSection(
                message = state.projectionMessage,
                date = state.projectionDate,
                bars = state.projectionBars,
                progress = state.progress,
            )
            LinkedChallengesSection(
                challenges = state.linkedChallenges,
                onChallengeClick = onChallengeClick,
                onSeeAll = onSeeAllChallenges,
            )
            QuickAddButton(label = state.quickAddLabel, onClick = onQuickAdd)
        }
    }

    if (showAddDialog) {
        AmountDialog(
            title = "Ajouter à l'objectif",
            confirmLabel = "Ajouter",
            onConfirm = { cents ->
                onAddManual(cents)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
    if (showAutoSaveDialog) {
        AutoSaveDialog(
            onConfirm = { cents, cadence ->
                onSetAutoSave(cents, cadence)
                showAutoSaveDialog = false
            },
            onDismiss = { showAutoSaveDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDetailTopBar(onBack: () -> Unit, onEdit: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WisepennyColors.BackgroundPrimary,
            navigationIconContentColor = WisepennyColors.TextPrimary,
            actionIconContentColor = WisepennyColors.TextPrimary,
        ),
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.clearAndSetSemantics { contentDescription = "Retour" },
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.clearAndSetSemantics { contentDescription = "Modifier l'objectif" },
            ) {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
            }
        },
    )
}

@Composable
private fun GoalHeader(category: String, name: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.AccentMint,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextPrimary,
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextTertiary,
            )
        }
    }
}

@Composable
private fun GoalHeroCard(state: GoalDetailUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WisepennyColors.SurfaceLight),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "ÉPARGNÉ",
                style = MaterialTheme.typography.labelSmall,
                color = WisepennyColors.TextOnLightMuted,
            )
            Text(
                text = state.savedLabel,
                style = MaterialTheme.typography.displayLarge,
                color = WisepennyColors.TextOnLight,
            )
            Text(
                text = state.targetLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextOnLightMuted,
            )
            SavingsProgressBar(
                progress = state.progress,
                trackColor = WisepennyColors.SurfaceLightAlt,
                modifier = Modifier.padding(top = Spacing.xs),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.xxs),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = state.percentLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextOnLight,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = state.remainingLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextOnLight,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ActionCardsRow(onAdd: () -> Unit, onProgram: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        ActionCard(
            icon = { Icon(Icons.Filled.Add, contentDescription = null, tint = WisepennyColors.AccentMint) },
            title = "Ajouter",
            subtitle = "manuellement",
            onClick = onAdd,
            modifier = Modifier.weight(1f),
        )
        ActionCard(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = WisepennyColors.AccentMint) },
            title = "Programmer",
            subtitle = "auto-épargne",
            onClick = onProgram,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WisepennyColors.SurfaceElevated),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(WisepennyColors.BackgroundPrimary),
                contentAlignment = Alignment.Center,
            ) { icon() }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = WisepennyColors.TextTertiary,
            )
        }
    }
}

@Composable
private fun ProjectionSection(message: String, date: String?, bars: List<Float>, progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Projection")
        Card(
            colors = CardDefaults.cardColors(containerColor = WisepennyColors.SurfaceElevated),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(message)
                        if (date != null) {
                            append(" ")
                            withStyle(SpanStyle(color = WisepennyColors.AccentMint, fontWeight = FontWeight.Bold)) {
                                append(date)
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextSecondary,
                )
                if (bars.isNotEmpty()) {
                    val currentIndex = (progress.coerceIn(0f, 1f) * (bars.size - 1)).roundToInt()
                    ProjectionChart(bars = bars, currentIndex = currentIndex)
                }
            }
        }
    }
}

@Composable
private fun ProjectionChart(bars: List<Float>, currentIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        bars.forEachIndexed { index, fraction ->
            val color =
                if (index == currentIndex) WisepennyColors.AccentMint else WisepennyColors.BorderSubtle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height((80 * fraction.coerceIn(0.08f, 1f)).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun LinkedChallengesSection(
    challenges: List<LinkedChallengeItem>,
    onChallengeClick: (Long) -> Unit,
    onSeeAll: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Défis liés",
                style = MaterialTheme.typography.headlineMedium,
                color = WisepennyColors.TextPrimary,
            )
            Text(
                text = "Voir tout ↗",
                style = MaterialTheme.typography.labelSmall,
                color = WisepennyColors.AccentMint,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onSeeAll),
            )
        }
        if (challenges.isEmpty()) {
            Text(
                text = "Aucun défi lié pour l'instant",
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextTertiary,
            )
        } else {
            challenges.forEach { challenge ->
                LinkedChallengeRow(challenge = challenge, onClick = { onChallengeClick(challenge.id) })
            }
        }
    }
}

@Composable
private fun LinkedChallengeRow(challenge: LinkedChallengeItem, onClick: () -> Unit) {
    val onCard = if (challenge.isStarted) WisepennyColors.TextOnLight else WisepennyColors.TextPrimary
    val muted =
        if (challenge.isStarted) WisepennyColors.TextOnLightMuted else WisepennyColors.TextTertiary
    val container =
        if (challenge.isStarted) WisepennyColors.SurfaceLight else WisepennyColors.SurfaceElevated
    val iconBg =
        if (challenge.isStarted) WisepennyColors.SurfaceLightAlt else WisepennyColors.BackgroundPrimary

    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = goalEmoji(challenge.iconKey), style = MaterialTheme.typography.bodyMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onCard,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = challenge.contributionLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = muted,
                )
            }
            if (challenge.isStarted && challenge.statusLabel != null) {
                StatusPill(text = challenge.statusLabel)
            } else {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WisepennyColors.SurfaceLight,
                        contentColor = WisepennyColors.TextOnLight,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(text = "Démarrer", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun StatusPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(WisepennyColors.AccentMintSoft)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextOnLight,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun QuickAddButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = WisepennyColors.AccentMint,
            contentColor = WisepennyColors.TextOnLight,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
        Text(text = "$label ↗", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = WisepennyColors.TextPrimary,
    )
}

@Composable
private fun AmountDialog(
    title: String,
    confirmLabel: String,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val cents = text.filter { it.isDigit() }.toLongOrNull()?.times(100)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.filter { c -> c.isDigit() } },
                label = { Text("Montant en €") },
                singleLine = true,
            )
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
private fun AutoSaveDialog(
    onConfirm: (Long, SavingsCadence) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var cadence by remember { mutableStateOf(SavingsCadence.WEEKLY) }
    val cents = text.filter { it.isDigit() }.toLongOrNull()?.times(100)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Programmer une auto-épargne") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() } },
                    label = { Text("Montant en €") },
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    CadenceChip("Par semaine", cadence == SavingsCadence.WEEKLY) {
                        cadence = SavingsCadence.WEEKLY
                    }
                    CadenceChip("Par mois", cadence == SavingsCadence.MONTHLY) {
                        cadence = SavingsCadence.MONTHLY
                    }
                }
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

@Composable
private fun CadenceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = WisepennyColors.AccentMint,
                contentColor = WisepennyColors.TextOnLight,
            ),
            shape = RoundedCornerShape(12.dp),
        ) { Text(label, style = MaterialTheme.typography.labelSmall) }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
        ) { Text(label, style = MaterialTheme.typography.labelSmall, color = WisepennyColors.TextSecondary) }
    }
}

@Preview
@Composable
private fun GoalDetailScreenPreview() {
    WisepennyTheme {
        GoalDetailScreen(
            state = GoalDetailUiState(
                id = 1,
                category = "Voyage",
                name = "Lisbonne 2026",
                subtitle = "Avec Sarah et Tom",
                savedLabel = "450 €",
                targetLabel = "sur 800 €",
                progress = 0.56f,
                percentLabel = "56 % Atteint",
                remainingLabel = "350 € Restant",
                projectionMessage = "Tu atteindras ton objectif le",
                projectionDate = "15 mars 2026",
                projectionBars = listOf(0.2f, 0.35f, 0.5f, 0.7f, 0.85f, 1f),
                linkedChallenges = listOf(
                    LinkedChallengeItem(1, "coffee", "7 jours sans café", "+15 € vers Lisbonne", "Jour 5/7", true),
                    LinkedChallengeItem(2, "shopping", "Pas d'achat impulsif", "+20 € vers Lisbonne", null, false),
                ),
                quickAddLabel = "Ajouter 10 € maintenant",
            ),
            onBack = {},
            onEdit = {},
            onQuickAdd = {},
            onAddManual = {},
            onSetAutoSave = { _, _ -> },
            onChallengeClick = {},
            onSeeAllChallenges = {},
        )
    }
}
