package com.wisepenny.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.wisepenny.data.mapper.toDomain
import com.wisepenny.db.WisepennyDatabase
import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class ChallengeRepositoryImpl(
    private val database: WisepennyDatabase,
) : ChallengeRepository {

    private val queries = database.challengeQueries
    private val goalQueries = database.goalQueries

    override fun observeActive(): Flow<Challenge?> = queries.selectActive()
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { row -> row?.toDomain() }

    override fun observeByGoal(goalId: Long): Flow<List<Challenge>> = queries.selectByGoal(goalId)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { it.toDomain() } }

    override suspend fun create(
        title: String,
        subtitle: String,
        dailyAmountCents: Long,
        totalDays: Int,
        startDate: LocalDate,
        goalId: Long?,
    ) {
        withContext(Dispatchers.IO) {
            queries.insertChallenge(
                title = title,
                subtitle = subtitle,
                dailyAmountCents = dailyAmountCents,
                totalDays = totalDays.toLong(),
                startDate = startDate.toString(),
                goalId = goalId,
            )
        }
    }

    override suspend fun completeToday(challengeId: Long) {
        withContext(Dispatchers.IO) {
            queries.transaction {
                queries.incrementCompletedDays(challengeId)
                // Cross-feature loop: if this challenge feeds a savings goal,
                // its daily amount also lands in that goal. No-op when unlinked,
                // so the standalone Challenge screen behaves exactly as before.
                val challenge = queries.selectById(challengeId).executeAsOneOrNull()
                val goalId = challenge?.goalId
                if (goalId != null) {
                    goalQueries.addContribution(amount = challenge.dailyAmountCents, id = goalId)
                }
            }
        }
    }

    override suspend fun linkToGoal(challengeId: Long, goalId: Long) {
        withContext(Dispatchers.IO) {
            queries.linkToGoal(goalId = goalId, id = challengeId)
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}
