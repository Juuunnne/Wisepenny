package com.wisepenny.domain.repository

import com.wisepenny.domain.model.Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ChallengeRepository {

    fun observeActive(): Flow<Challenge?>

    fun observeByGoal(goalId: Long): Flow<List<Challenge>>

    suspend fun create(
        title: String,
        subtitle: String,
        dailyAmountCents: Long,
        totalDays: Int,
        startDate: LocalDate,
        goalId: Long? = null,
    )

    suspend fun completeToday(challengeId: Long)

    suspend fun linkToGoal(challengeId: Long, goalId: Long)

    suspend fun clear()
}
