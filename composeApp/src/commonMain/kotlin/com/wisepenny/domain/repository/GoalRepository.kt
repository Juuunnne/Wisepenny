package com.wisepenny.domain.repository

import com.wisepenny.domain.model.Goal
import com.wisepenny.domain.model.SavingsCadence
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GoalRepository {

    fun observeAll(): Flow<List<Goal>>

    fun observeById(id: Long): Flow<Goal?>

    suspend fun create(
        name: String,
        category: String,
        subtitle: String,
        iconKey: String,
        targetAmountCents: Long,
        isPriority: Boolean,
        targetDate: LocalDate?,
        createdDate: LocalDate,
    ): Long

    suspend fun addContribution(goalId: Long, amountCents: Long)

    suspend fun setAutoSave(
        goalId: Long,
        amountCents: Long,
        cadence: SavingsCadence,
        today: LocalDate,
    )

    /**
     * Lazily applies any auto-save contributions that have come due since each
     * goal's [Goal.autoSaveLastAppliedDate]. Call once on load (e.g. from the
     * ViewModel's init) — there is no OS scheduler yet, so saving "happens"
     * whenever the app opens. Idempotent: advancing the applied-date means a
     * second call on the same day adds nothing.
     */
    suspend fun applyDueAutoSaves(today: LocalDate)

    suspend fun clear()
}
