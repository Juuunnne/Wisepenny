package com.wisepenny.data.mapper

import com.wisepenny.domain.model.Challenge
import kotlinx.datetime.LocalDate
import com.wisepenny.db.Challenge as ChallengeRow

fun ChallengeRow.toDomain(): Challenge = Challenge(
    id = id,
    title = title,
    subtitle = subtitle,
    dailyAmountCents = dailyAmountCents,
    totalDays = totalDays.toInt(),
    completedDays = completedDays.toInt(),
    startDate = LocalDate.parse(startDate),
    goalId = goalId,
)
