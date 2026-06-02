package com.wisepenny.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ContributionRepository {

    /** Total cents contributed in the half-open date window [startInclusive, endExclusive). */
    fun observeSumBetween(startInclusive: LocalDate, endExclusive: LocalDate): Flow<Long>

    suspend fun clear()
}
