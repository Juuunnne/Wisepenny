package com.wisepenny.domain.model

import kotlinx.datetime.LocalDate

data class Goal(
    val id: Long,
    val name: String,
    val category: String,
    val subtitle: String,
    val iconKey: String,
    val targetAmountCents: Long,
    val savedAmountCents: Long,
    val isPriority: Boolean,
    val targetDate: LocalDate?,
    val autoSaveAmountCents: Long?,
    val autoSaveCadence: SavingsCadence?,
    val createdDate: LocalDate,
    val autoSaveLastAppliedDate: LocalDate?,
)

enum class SavingsCadence(val days: Int) {
    WEEKLY(7),
    MONTHLY(30),
}
