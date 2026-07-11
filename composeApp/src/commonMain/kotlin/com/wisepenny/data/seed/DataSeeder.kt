package com.wisepenny.data.seed

import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.domain.repository.ChallengeRepository
import com.wisepenny.domain.repository.GoalRepository
import com.wisepenny.domain.repository.ModuleProgressRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * Single source of demo data. Since Step 8 the demo world is seeded when the
 * onboarding wizard completes (see [seedDemoWorld]), not on every launch — the
 * first-run flow creates the user's real goal, then this fills the app around it.
 *
 * Seeding lives here rather than in a ViewModel because, under real navigation,
 * a tab's ViewModel is created lazily — a user who starts on the dashboard would
 * otherwise never trigger seeding and would see an empty app.
 */
class DataSeeder(
    private val goalRepository: GoalRepository,
    private val challengeRepository: ChallengeRepository,
    private val moduleProgressRepository: ModuleProgressRepository,
) {

    /** Run on every launch of an already-onboarded app: no seeding, just catch-up work. */
    suspend fun onAppStart() {
        // Apply any auto-save contributions that have come due since last open.
        goalRepository.applyDueAutoSaves(today())
    }

    /**
     * Seed the demo goals/challenges/modules. Called once from the onboarding
     * wizard's completion, before the user's own goal is created. The empty-goals
     * guard keeps it safe if a reset→re-onboard runs it again.
     */
    suspend fun seedDemoWorld(today: LocalDate = today()) {
        if (goalRepository.observeAll().first().isNotEmpty()) return

        val createdOn = today.minus(3, DateTimeUnit.MONTH)
        val backDate = createdOn          // old enough to sit outside this/last month
        val lastMonth = today.minus(1, DateTimeUnit.MONTH)

        val japonId = goalRepository.create(
            name = "Voyage au Japon",
            category = "Voyage",
            subtitle = "",
            iconKey = "travel",
            targetAmountCents = 3_000_00,
            // Since Step 8 the user's onboarding goal is the single priority goal.
            isPriority = false,
            targetDate = null,
            createdDate = createdOn,
        )
        fund(japonId, today, backDate, lastMonth, backCents = 1_166_00, lastCents = 40_00, thisCents = 44_00)

        val ordiId = goalRepository.create(
            name = "Nouvel ordi",
            category = "Tech",
            subtitle = "",
            iconKey = "laptop",
            targetAmountCents = 1_200_00,
            isPriority = false,
            targetDate = null,
            createdDate = createdOn,
        )
        fund(ordiId, today, backDate, lastMonth, backCents = 392_00, lastCents = 28_00, thisCents = 30_00)

        val lisbonneId = goalRepository.create(
            name = "Lisbonne 2026",
            category = "Voyage",
            subtitle = "Avec Sarah et Tom",
            iconKey = "travel",
            targetAmountCents = 800_00,
            isPriority = false,
            targetDate = null,
            createdDate = createdOn,
        )
        fund(lisbonneId, today, backDate, lastMonth, backCents = 380_00, lastCents = 40_00, thisCents = 30_00)
        goalRepository.setAutoSave(lisbonneId, 50_00, SavingsCadence.WEEKLY, today)

        // Two challenges feeding Lisbonne, advanced to match the dashboard mockup.
        challengeRepository.create(
            title = "7 jours sans café",
            subtitle = "Économise 21 € en une semaine",
            dailyAmountCents = 300,
            totalDays = 7,
            startDate = today,
            goalId = lisbonneId,
        )
        challengeRepository.create(
            title = "Pas d'achats impulsifs",
            subtitle = "Résiste aux achats spontanés",
            dailyAmountCents = 400,
            totalDays = 7,
            startDate = today,
            goalId = lisbonneId,
        )

        val linked = challengeRepository.observeByGoal(lisbonneId).first()
        val coffee = linked.firstOrNull { it.title.contains("café", ignoreCase = true) }
        val impulsif = linked.firstOrNull { it.title.contains("impuls", ignoreCase = true) }
        repeat(5) { coffee?.let { challengeRepository.completeToday(it.id, today) } }
        repeat(2) { impulsif?.let { challengeRepository.completeToday(it.id, today) } }

        // Start the first learning module at 40 % (2 of its 5 pages) to match the
        // Apprendre mockup; the rest stay locked until it's completed.
        moduleProgressRepository.saveProgress(
            moduleId = "bourse",
            pagesRead = 2,
            completed = false,
            completedDate = null,
        )
    }

    private suspend fun fund(
        goalId: Long,
        today: LocalDate,
        backDate: LocalDate,
        lastMonth: LocalDate,
        backCents: Long,
        lastCents: Long,
        thisCents: Long,
    ) {
        if (backCents > 0) goalRepository.addContribution(goalId, backCents, backDate, "seed")
        if (lastCents > 0) goalRepository.addContribution(goalId, lastCents, lastMonth, "seed")
        if (thisCents > 0) goalRepository.addContribution(goalId, thisCents, today, "seed")
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}
