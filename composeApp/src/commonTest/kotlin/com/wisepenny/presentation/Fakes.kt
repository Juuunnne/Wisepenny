package com.wisepenny.presentation

import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.model.Goal
import com.wisepenny.domain.model.LearningModule
import com.wisepenny.domain.model.ModuleProgress
import com.wisepenny.domain.model.Profile
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.domain.repository.ChallengeRepository
import com.wisepenny.domain.repository.ContributionRepository
import com.wisepenny.domain.repository.GoalRepository
import com.wisepenny.domain.repository.ModuleContentRepository
import com.wisepenny.domain.repository.ModuleProgressRepository
import com.wisepenny.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.datetime.LocalDate

/**
 * In-memory fakes for the domain repositories, backed by [MutableStateFlow] so the
 * ViewModels under test see live updates exactly as they would with the real DB. Each
 * fake also records the write calls it received, so tests can assert on side effects
 * (e.g. "onQuickAdd added 10 € to the selected goal").
 */

class FakeGoalRepository(initial: List<Goal> = emptyList()) : GoalRepository {
    val goals = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1

    data class Contribution(val goalId: Long, val amountCents: Long, val date: LocalDate, val source: String)
    data class AutoSave(val goalId: Long, val amountCents: Long, val cadence: SavingsCadence, val today: LocalDate)

    val contributions = mutableListOf<Contribution>()
    val autoSaves = mutableListOf<AutoSave>()
    val appliedDueDates = mutableListOf<LocalDate>()
    var clearCount = 0

    override fun observeAll(): Flow<List<Goal>> = goals

    override fun observeById(id: Long): Flow<Goal?> = goals.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun create(
        name: String,
        category: String,
        subtitle: String,
        iconKey: String,
        targetAmountCents: Long,
        isPriority: Boolean,
        targetDate: LocalDate?,
        createdDate: LocalDate,
    ): Long {
        val id = nextId++
        goals.value = goals.value + Goal(
            id = id,
            name = name,
            category = category,
            subtitle = subtitle,
            iconKey = iconKey,
            targetAmountCents = targetAmountCents,
            savedAmountCents = 0L,
            isPriority = isPriority,
            targetDate = targetDate,
            autoSaveAmountCents = null,
            autoSaveCadence = null,
            createdDate = createdDate,
            autoSaveLastAppliedDate = null,
        )
        return id
    }

    override suspend fun addContribution(goalId: Long, amountCents: Long, date: LocalDate, source: String) {
        contributions += Contribution(goalId, amountCents, date, source)
        goals.value = goals.value.map {
            if (it.id == goalId) it.copy(savedAmountCents = it.savedAmountCents + amountCents) else it
        }
    }

    override suspend fun setAutoSave(goalId: Long, amountCents: Long, cadence: SavingsCadence, today: LocalDate) {
        autoSaves += AutoSave(goalId, amountCents, cadence, today)
        goals.value = goals.value.map {
            if (it.id == goalId) {
                it.copy(
                    autoSaveAmountCents = amountCents,
                    autoSaveCadence = cadence,
                    autoSaveLastAppliedDate = today,
                )
            } else it
        }
    }

    override suspend fun applyDueAutoSaves(today: LocalDate) {
        appliedDueDates += today
    }

    override suspend fun clear() {
        clearCount++
        goals.value = emptyList()
    }
}

class FakeChallengeRepository(initial: List<Challenge> = emptyList()) : ChallengeRepository {
    val challenges = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1

    data class Completion(val challengeId: Long, val today: LocalDate)

    val completions = mutableListOf<Completion>()
    var clearCount = 0

    override fun observeActive(): Flow<Challenge?> = challenges.map { it.firstOrNull() }

    override fun observeByGoal(goalId: Long): Flow<List<Challenge>> =
        challenges.map { list -> list.filter { it.goalId == goalId } }

    override fun observeActiveChallenges(): Flow<List<Challenge>> = challenges

    override suspend fun create(
        title: String,
        subtitle: String,
        dailyAmountCents: Long,
        totalDays: Int,
        startDate: LocalDate,
        goalId: Long?,
    ) {
        val id = nextId++
        challenges.value = challenges.value + Challenge(
            id = id,
            title = title,
            subtitle = subtitle,
            dailyAmountCents = dailyAmountCents,
            totalDays = totalDays,
            completedDays = 0,
            startDate = startDate,
            goalId = goalId,
        )
    }

    override suspend fun completeToday(challengeId: Long, today: LocalDate) {
        completions += Completion(challengeId, today)
        challenges.value = challenges.value.map {
            if (it.id == challengeId) it.copy(completedDays = it.completedDays + 1) else it
        }
    }

    override suspend fun linkToGoal(challengeId: Long, goalId: Long) {
        challenges.value = challenges.value.map {
            if (it.id == challengeId) it.copy(goalId = goalId) else it
        }
    }

    override suspend fun clear() {
        clearCount++
        challenges.value = emptyList()
    }
}

/**
 * The dashboard queries this twice — once for the current month, once for the previous
 * month. It distinguishes the two windows by whether [today] falls inside the window,
 * so tests only set the two totals rather than juggle real month boundaries.
 */
class FakeContributionRepository(
    var thisMonthCents: Long = 0L,
    var lastMonthCents: Long = 0L,
    private val today: LocalDate,
) : ContributionRepository {
    var clearCount = 0

    override fun observeSumBetween(startInclusive: LocalDate, endExclusive: LocalDate): Flow<Long> {
        val isCurrentWindow = today >= startInclusive && today < endExclusive
        return MutableStateFlow(if (isCurrentWindow) thisMonthCents else lastMonthCents)
    }

    override suspend fun clear() {
        clearCount++
    }
}

class FakeProfileRepository(initial: Profile? = null) : ProfileRepository {
    val profile = MutableStateFlow(initial)
    var clearCount = 0

    override fun observe(): Flow<Profile?> = profile

    override suspend fun save(
        firstName: String,
        motivation: String,
        createdDate: LocalDate,
        onboardingCompleted: Boolean,
        currency: String,
        notificationsOptIn: Boolean,
        bankLinked: Boolean,
    ) {
        profile.value = Profile(
            firstName = firstName,
            motivation = motivation,
            createdDate = createdDate,
            onboardingCompleted = onboardingCompleted,
            currency = currency,
            notificationsOptIn = notificationsOptIn,
            bankLinked = bankLinked,
        )
    }

    override suspend fun clear() {
        clearCount++
        profile.value = null
    }
}

class FakeModuleProgressRepository(initial: List<ModuleProgress> = emptyList()) : ModuleProgressRepository {
    val progress = MutableStateFlow(initial)

    data class Saved(val moduleId: String, val pagesRead: Int, val completed: Boolean, val completedDate: LocalDate?)

    val saves = mutableListOf<Saved>()
    var clearCount = 0

    override fun observeAll(): Flow<List<ModuleProgress>> = progress

    override suspend fun saveProgress(
        moduleId: String,
        pagesRead: Int,
        completed: Boolean,
        completedDate: LocalDate?,
    ) {
        saves += Saved(moduleId, pagesRead, completed, completedDate)
        val existing = progress.value.firstOrNull { it.moduleId == moduleId }
        val updated = ModuleProgress(moduleId, pagesRead, completed, completedDate)
        progress.value = if (existing == null) {
            progress.value + updated
        } else {
            progress.value.map { if (it.moduleId == moduleId) updated else it }
        }
    }

    override suspend fun clear() {
        clearCount++
        progress.value = emptyList()
    }
}

class FakeModuleContentRepository(private val modules: List<LearningModule> = emptyList()) : ModuleContentRepository {
    override suspend fun loadModules(): List<LearningModule> = modules
}

/**
 * Subscribes to [flow] for the lifetime of the test so its `WhileSubscribed` sharing
 * actually starts producing. Call [kotlinx.coroutines.test.runTest]'s `advanceUntilIdle`
 * afterwards, then read `flow.value`.
 */
fun <T> TestScope.subscribe(flow: StateFlow<T>) {
    backgroundScope.launch { flow.collect {} }
}
