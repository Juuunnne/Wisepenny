package com.wisepenny.domain.repository

import com.wisepenny.domain.model.Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ChallengeRepository {

    fun observeActive(): Flow<Challenge?>

    suspend fun create(
        title: String,
        subtitle: String,
        dailyAmountCents: Long,
        totalDays: Int,
        startDate: LocalDate,
    )

    suspend fun completeToday(challengeId: Long)

    suspend fun clear()
}
