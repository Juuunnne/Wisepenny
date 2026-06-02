package com.wisepenny.data.mapper

import com.wisepenny.domain.model.Goal
import com.wisepenny.domain.model.SavingsCadence
import kotlinx.datetime.LocalDate
import com.wisepenny.db.Goal as GoalRow

fun GoalRow.toDomain(): Goal = Goal(
    id = id,
    name = name,
    category = category,
    subtitle = subtitle,
    iconKey = iconKey,
    targetAmountCents = targetAmountCents,
    savedAmountCents = savedAmountCents,
    isPriority = isPriority == 1L,
    targetDate = targetDate?.let(LocalDate::parse),
    autoSaveAmountCents = autoSaveAmountCents,
    autoSaveCadence = autoSaveCadence?.let { SavingsCadence.valueOf(it) },
    createdDate = LocalDate.parse(createdDate),
    autoSaveLastAppliedDate = autoSaveLastAppliedDate?.let(LocalDate::parse),
)
