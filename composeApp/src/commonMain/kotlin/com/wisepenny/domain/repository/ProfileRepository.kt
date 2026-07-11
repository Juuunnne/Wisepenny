package com.wisepenny.domain.repository

import com.wisepenny.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ProfileRepository {

    /** Emits the single profile row, or null until the wizard has completed. */
    fun observe(): Flow<Profile?>

    suspend fun save(
        motivation: String,
        createdDate: LocalDate,
        onboardingCompleted: Boolean,
        currency: String,
        notificationsOptIn: Boolean,
        bankLinked: Boolean,
    )

    suspend fun clear()
}
