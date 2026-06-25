package com.wisepenny.domain.model

import kotlinx.datetime.LocalDate

/**
 * A user's progress through a learning module. [pagesRead] is the furthest page
 * count reached (drives the list's progress bar and the reader's resume point);
 * [completed] gates the next module's unlock.
 */
data class ModuleProgress(
    val moduleId: String,
    val pagesRead: Int,
    val completed: Boolean,
    val completedDate: LocalDate?,
)
