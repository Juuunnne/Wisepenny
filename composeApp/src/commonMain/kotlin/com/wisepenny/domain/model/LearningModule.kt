package com.wisepenny.domain.model

/**
 * A narrative learning module (Brilliant.org-style) — a short, story-driven
 * lesson made of scrollable concept [pages]. No quizzes (CONTEXT §2). The
 * catalogue is static content, loaded from a bundled JSON file.
 */
data class LearningModule(
    val id: String,
    val title: String,
    val description: String,
    val level: ModuleLevel,
    val pages: List<ModulePage>,
)

/** One concept page: a heading, the explanation [body], and an optional worked [example]. */
data class ModulePage(
    val heading: String,
    val body: String,
    val example: String?,
)

enum class ModuleLevel(val label: String) {
    DEBUTANT("Débutant"),
    INTERMEDIAIRE("Intermédiaire"),
    AVANCE("Avancé"),
}
