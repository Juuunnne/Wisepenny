package com.wisepenny.presentation

import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.model.Goal
import com.wisepenny.domain.model.LearningModule
import com.wisepenny.domain.model.ModuleLevel
import com.wisepenny.domain.model.ModulePage
import com.wisepenny.domain.model.Profile
import com.wisepenny.domain.model.SavingsCadence
import kotlinx.datetime.LocalDate

/** Test builders with sensible defaults so each test only spells out the fields it cares about. */

fun aGoal(
    id: Long = 1L,
    name: String = "Objectif",
    category: String = "Épargne",
    subtitle: String = "",
    iconKey: String = "target",
    targetAmountCents: Long = 100_00L,
    savedAmountCents: Long = 0L,
    isPriority: Boolean = false,
    targetDate: LocalDate? = null,
    autoSaveAmountCents: Long? = null,
    autoSaveCadence: SavingsCadence? = null,
    createdDate: LocalDate = LocalDate(2026, 1, 1),
    autoSaveLastAppliedDate: LocalDate? = null,
) = Goal(
    id = id,
    name = name,
    category = category,
    subtitle = subtitle,
    iconKey = iconKey,
    targetAmountCents = targetAmountCents,
    savedAmountCents = savedAmountCents,
    isPriority = isPriority,
    targetDate = targetDate,
    autoSaveAmountCents = autoSaveAmountCents,
    autoSaveCadence = autoSaveCadence,
    createdDate = createdDate,
    autoSaveLastAppliedDate = autoSaveLastAppliedDate,
)

fun aChallenge(
    id: Long = 1L,
    title: String = "7 jours sans café",
    subtitle: String = "",
    dailyAmountCents: Long = 300L,
    totalDays: Int = 7,
    completedDays: Int = 0,
    startDate: LocalDate = LocalDate(2026, 1, 1),
    goalId: Long? = null,
) = Challenge(
    id = id,
    title = title,
    subtitle = subtitle,
    dailyAmountCents = dailyAmountCents,
    totalDays = totalDays,
    completedDays = completedDays,
    startDate = startDate,
    goalId = goalId,
)

fun aProfile(
    firstName: String = "Alex",
    motivation: String = "Voyager",
    createdDate: LocalDate = LocalDate(2026, 1, 1),
    onboardingCompleted: Boolean = true,
    currency: String = "EUR",
    notificationsOptIn: Boolean = false,
    bankLinked: Boolean = false,
) = Profile(
    firstName = firstName,
    motivation = motivation,
    createdDate = createdDate,
    onboardingCompleted = onboardingCompleted,
    currency = currency,
    notificationsOptIn = notificationsOptIn,
    bankLinked = bankLinked,
)

fun aModule(
    id: String,
    title: String = "Module $id",
    description: String = "",
    level: ModuleLevel = ModuleLevel.DEBUTANT,
    pageCount: Int = 3,
) = LearningModule(
    id = id,
    title = title,
    description = description,
    level = level,
    pages = List(pageCount) { ModulePage("Page $it", "Body $it", null) },
)
