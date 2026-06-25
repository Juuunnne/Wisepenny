package com.wisepenny.domain.repository

import com.wisepenny.domain.model.ModuleProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ModuleProgressRepository {

    fun observeAll(): Flow<List<ModuleProgress>>

    /**
     * Records that the user has read [pagesRead] pages of [moduleId]. Idempotent
     * and monotonic: the store keeps the furthest progress and, once [completed],
     * stays completed (see ModuleProgress.sq `upsert`).
     */
    suspend fun saveProgress(
        moduleId: String,
        pagesRead: Int,
        completed: Boolean,
        completedDate: LocalDate?,
    )

    suspend fun clear()
}
