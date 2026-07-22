package com.wisepenny.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.wisepenny.data.mapper.toDomain
import com.wisepenny.db.WisepennyDatabase
import com.wisepenny.domain.model.Profile
import com.wisepenny.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class ProfileRepositoryImpl(
    database: WisepennyDatabase,
) : ProfileRepository {

    private val queries = database.profileQueries

    override fun observe(): Flow<Profile?> = queries.selectProfile()
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { row -> row?.toDomain() }

    override suspend fun save(
        firstName: String,
        motivation: String,
        createdDate: LocalDate,
        onboardingCompleted: Boolean,
        currency: String,
        notificationsOptIn: Boolean,
        bankLinked: Boolean,
    ) {
        withContext(Dispatchers.IO) {
            queries.upsertProfile(
                motivation = motivation,
                createdDate = createdDate.toString(),
                onboardingCompleted = if (onboardingCompleted) 1L else 0L,
                currency = currency,
                notificationsOptIn = if (notificationsOptIn) 1L else 0L,
                bankLinked = if (bankLinked) 1L else 0L,
                firstName = firstName,
            )
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}
