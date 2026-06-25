package com.wisepenny.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wisepenny.data.mapper.toDomain
import com.wisepenny.db.WisepennyDatabase
import com.wisepenny.domain.model.ModuleProgress
import com.wisepenny.domain.repository.ModuleProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class ModuleProgressRepositoryImpl(
    database: WisepennyDatabase,
) : ModuleProgressRepository {

    private val queries = database.moduleProgressQueries

    override fun observeAll(): Flow<List<ModuleProgress>> = queries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { it.toDomain() } }

    override suspend fun saveProgress(
        moduleId: String,
        pagesRead: Int,
        completed: Boolean,
        completedDate: LocalDate?,
    ) {
        withContext(Dispatchers.IO) {
            queries.transaction {
                // Monotonic merge: keep the furthest page reached and, once completed,
                // stay completed — a re-read must never re-lock the next module.
                val existing = queries.selectById(moduleId).executeAsOneOrNull()
                queries.insertOrReplace(
                    moduleId = moduleId,
                    pagesRead = maxOf(pagesRead.toLong(), existing?.pagesRead ?: 0L),
                    completed = if (completed || existing?.completed == 1L) 1L else 0L,
                    completedDate = completedDate?.toString() ?: existing?.completedDate,
                )
            }
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}
