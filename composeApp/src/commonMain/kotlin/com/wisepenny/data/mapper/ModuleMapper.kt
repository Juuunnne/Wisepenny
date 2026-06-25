package com.wisepenny.data.mapper

import com.wisepenny.data.content.ModuleDto
import com.wisepenny.data.content.ModulePageDto
import com.wisepenny.domain.model.LearningModule
import com.wisepenny.domain.model.ModuleLevel
import com.wisepenny.domain.model.ModulePage
import com.wisepenny.domain.model.ModuleProgress
import kotlinx.datetime.LocalDate
import com.wisepenny.db.ModuleProgress as ModuleProgressRow

fun ModuleDto.toDomain(): LearningModule = LearningModule(
    id = id,
    title = title,
    description = description,
    level = level.toModuleLevel(),
    pages = pages.map { it.toDomain() },
)

private fun ModulePageDto.toDomain(): ModulePage = ModulePage(
    heading = heading,
    body = body,
    example = example,
)

private fun String.toModuleLevel(): ModuleLevel = when (uppercase()) {
    "INTERMEDIAIRE" -> ModuleLevel.INTERMEDIAIRE
    "AVANCE" -> ModuleLevel.AVANCE
    else -> ModuleLevel.DEBUTANT
}

fun ModuleProgressRow.toDomain(): ModuleProgress = ModuleProgress(
    moduleId = moduleId,
    pagesRead = pagesRead.toInt(),
    completed = completed == 1L,
    completedDate = completedDate?.let(LocalDate::parse),
)
