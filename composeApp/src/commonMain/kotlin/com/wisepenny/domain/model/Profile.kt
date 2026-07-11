package com.wisepenny.domain.model

import kotlinx.datetime.LocalDate

/**
 * The user's onboarding profile — a single record created when the first-launch
 * wizard completes. [onboardingCompleted] drives the startup gate in [com.wisepenny.App].
 */
data class Profile(
    val motivation: String,
    val createdDate: LocalDate,
    val onboardingCompleted: Boolean,
    val currency: String,
    val notificationsOptIn: Boolean,
    val bankLinked: Boolean,
)
