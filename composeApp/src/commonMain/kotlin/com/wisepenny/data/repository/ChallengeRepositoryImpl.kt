package com.wisepenny.data.repository

import app.cash.sqldelight.coroutines.asFlow
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
    database: WisepennyDatabase,
) : ChallengeRepository {

    private val queries = database.challengeQueries

    override fun observeActive(): Flow<Challenge?> = queries.selectActive()
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { row -> row?.toDomain() }

    override suspend fun create(
        title: String,
        subtitle: String,
        dailyAmountCents: Long,
        totalDays: Int,
        startDate: LocalDate,
    ) {
        withContext(Dispatchers.IO) {
            queries.insertChallenge(
                title = title,
                subtitle = subtitle,
                dailyAmountCents = dailyAmountCents,
                totalDays = totalDays.toLong(),
                startDate = startDate.toString(),
            )
        }
    }

    override suspend fun completeToday(challengeId: Long) {
        withContext(Dispatchers.IO) {
            queries.incrementCompletedDays(challengeId)
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}
