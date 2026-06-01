package com.wisepenny.domain.model

import kotlinx.datetime.LocalDate

data class Challenge(
    val id: Long,
    val title: String,
    val subtitle: String,
    val dailyAmountCents: Long,
    val totalDays: Int,
    val completedDays: Int,
    val startDate: LocalDate,
)
