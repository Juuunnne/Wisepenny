package com.wisepenny.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.wisepenny.data.mapper.toDomain
import com.wisepenny.db.WisepennyDatabase
import com.wisepenny.domain.model.Goal
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.domain.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

class GoalRepositoryImpl(
    database: WisepennyDatabase,
) : GoalRepository {

    private val queries = database.goalQueries

    override fun observeAll(): Flow<List<Goal>> = queries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Goal?> = queries.selectById(id)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { row -> row?.toDomain() }

    override suspend fun create(
        name: String,
        category: String,
        subtitle: String,
        iconKey: String,
        targetAmountCents: Long,
        isPriority: Boolean,
        targetDate: LocalDate?,
        createdDate: LocalDate,
    ): Long = withContext(Dispatchers.IO) {
        queries.transactionWithResult {
            queries.insertGoal(
                name = name,
                category = category,
                subtitle = subtitle,
                iconKey = iconKey,
                targetAmountCents = targetAmountCents,
                isPriority = if (isPriority) 1L else 0L,
                targetDate = targetDate?.toString(),
                createdDate = createdDate.toString(),
            )
            queries.lastInsertRowId().executeAsOne()
        }
    }

    override suspend fun addContribution(goalId: Long, amountCents: Long) {
        withContext(Dispatchers.IO) {
            queries.addContribution(amount = amountCents, id = goalId)
        }
    }

    override suspend fun setAutoSave(
        goalId: Long,
        amountCents: Long,
        cadence: SavingsCadence,
        today: LocalDate,
    ) {
        withContext(Dispatchers.IO) {
            queries.setAutoSave(
                amount = amountCents,
                cadence = cadence.name,
                date = today.toString(),
                id = goalId,
            )
        }
    }

    override suspend fun applyDueAutoSaves(today: LocalDate) {
        withContext(Dispatchers.IO) {
            val goals = queries.selectAll().executeAsList().map { it.toDomain() }
            for (goal in goals) {
                val amount = goal.autoSaveAmountCents ?: continue
                val cadence = goal.autoSaveCadence ?: continue
                val lastApplied = goal.autoSaveLastAppliedDate ?: continue
                val periods = lastApplied.daysUntil(today) / cadence.days
                if (periods <= 0) continue
                val remaining = (goal.targetAmountCents - goal.savedAmountCents).coerceAtLeast(0)
                val toAdd = (amount * periods).coerceAtMost(remaining)
                val newLastApplied = lastApplied.plus(periods * cadence.days, DateTimeUnit.DAY)
                queries.applyAutoSave(amount = toAdd, date = newLastApplied.toString(), id = goal.id)
            }
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}
