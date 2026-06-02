package com.wisepenny.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.wisepenny.db.WisepennyDatabase
import com.wisepenny.domain.repository.ContributionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class ContributionRepositoryImpl(
    database: WisepennyDatabase,
) : ContributionRepository {

    private val queries = database.contributionQueries

    override fun observeSumBetween(
        startInclusive: LocalDate,
        endExclusive: LocalDate,
    ): Flow<Long> = queries
        .sumBetween(startInclusive.toString(), endExclusive.toString())
        .asFlow()
        .mapToOne(Dispatchers.IO)

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}
