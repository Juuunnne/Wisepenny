package com.wisepenny.data.mapper

import com.wisepenny.domain.model.Profile
import kotlinx.datetime.LocalDate
import com.wisepenny.db.Profile as ProfileRow

fun ProfileRow.toDomain(): Profile = Profile(
    firstName = firstName,
    motivation = motivation,
    createdDate = LocalDate.parse(createdDate),
    onboardingCompleted = onboardingCompleted == 1L,
    currency = currency,
    notificationsOptIn = notificationsOptIn == 1L,
    bankLinked = bankLinked == 1L,
)
