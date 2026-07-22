package com.wisepenny.presentation.learning

import com.wisepenny.domain.model.ModuleProgress
import com.wisepenny.presentation.FakeModuleContentRepository
import com.wisepenny.presentation.FakeModuleProgressRepository
import com.wisepenny.presentation.aModule
import com.wisepenny.presentation.subscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LearningViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel(
        modules: List<com.wisepenny.domain.model.LearningModule>,
        progress: List<ModuleProgress> = emptyList(),
    ): Pair<LearningViewModel, FakeModuleProgressRepository> {
        val progressRepo = FakeModuleProgressRepository(progress)
        val vm = LearningViewModel(FakeModuleContentRepository(modules), progressRepo)
        return vm to progressRepo
    }

    @Test
    fun listState_isEmpty_untilCatalogLoads_thenPopulates() = runTest(dispatcher) {
        val (vm, _) = buildViewModel(listOf(aModule("bourse", pageCount = 5)))
        subscribe(vm.listState)

        // The catalogue load is dispatched but not yet run.
        assertTrue(vm.listState.value.modules.isEmpty())

        advanceUntilIdle()
        assertEquals(1, vm.listState.value.modules.size)
    }

    @Test
    fun linearUnlock_locksLaterModules_untilPreviousIsCompleted() = runTest(dispatcher) {
        val (vm, _) = buildViewModel(
            listOf(aModule("bourse", pageCount = 5), aModule("budget", pageCount = 3)),
            progress = listOf(ModuleProgress("bourse", pagesRead = 2, completed = false, completedDate = null)),
        )
        subscribe(vm.listState)
        advanceUntilIdle()

        val modules = vm.listState.value.modules
        val bourse = modules.first { it.id == "bourse" }
        val budget = modules.first { it.id == "budget" }
        assertEquals(ModuleStatus.ACTIVE, bourse.status) // first module always open
        assertEquals(40, bourse.progressPercent) // 2 of 5 pages
        assertEquals(ModuleStatus.LOCKED, budget.status) // prev not completed
    }

    @Test
    fun completedModule_reportsCompletedStatus_andUnlocksNext() = runTest(dispatcher) {
        val (vm, _) = buildViewModel(
            listOf(aModule("bourse", pageCount = 5), aModule("budget", pageCount = 3)),
            progress = listOf(ModuleProgress("bourse", pagesRead = 5, completed = true, completedDate = null)),
        )
        subscribe(vm.listState)
        advanceUntilIdle()

        val bourse = vm.listState.value.modules.first { it.id == "bourse" }
        val budget = vm.listState.value.modules.first { it.id == "budget" }
        assertEquals(ModuleStatus.COMPLETED, bourse.status)
        assertEquals(1f, bourse.progress)
        assertEquals(ModuleStatus.ACTIVE, budget.status)
    }

    @Test
    fun readerState_isNull_whenNoModuleSelected() = runTest(dispatcher) {
        val (vm, _) = buildViewModel(listOf(aModule("bourse", pageCount = 5)))
        subscribe(vm.readerState)
        advanceUntilIdle()

        assertNull(vm.readerState.value)
    }

    @Test
    fun readerState_resumesAtSavedPage_whenNotCompleted() = runTest(dispatcher) {
        val (vm, _) = buildViewModel(
            listOf(aModule("bourse", pageCount = 5)),
            progress = listOf(ModuleProgress("bourse", pagesRead = 2, completed = false, completedDate = null)),
        )
        subscribe(vm.readerState)
        vm.selectModule("bourse")
        advanceUntilIdle()

        assertEquals("bourse", vm.readerState.value?.moduleId)
        assertEquals(2, vm.readerState.value?.startIndex)
    }

    @Test
    fun readerState_restartsAtZero_whenModuleAlreadyCompleted() = runTest(dispatcher) {
        val (vm, _) = buildViewModel(
            listOf(aModule("bourse", pageCount = 5)),
            progress = listOf(ModuleProgress("bourse", pagesRead = 5, completed = true, completedDate = null)),
        )
        subscribe(vm.readerState)
        vm.selectModule("bourse")
        advanceUntilIdle()

        assertEquals(0, vm.readerState.value?.startIndex)
    }

    @Test
    fun onAdvance_savesIntermediateProgress_withoutCompleting() = runTest(dispatcher) {
        val (vm, progressRepo) = buildViewModel(listOf(aModule("bourse", pageCount = 5)))
        subscribe(vm.listState)
        advanceUntilIdle()

        vm.onAdvance("bourse", pageIndex = 0)
        advanceUntilIdle()

        val saved = progressRepo.saves.single()
        assertEquals(1, saved.pagesRead)
        assertFalse(saved.completed)
        assertNull(saved.completedDate)
    }

    @Test
    fun onAdvance_marksCompleted_onLastPage() = runTest(dispatcher) {
        val (vm, progressRepo) = buildViewModel(listOf(aModule("bourse", pageCount = 5)))
        subscribe(vm.listState)
        advanceUntilIdle()

        vm.onAdvance("bourse", pageIndex = 4) // last of 5 pages
        advanceUntilIdle()

        val saved = progressRepo.saves.single()
        assertEquals(5, saved.pagesRead)
        assertTrue(saved.completed)
        assertNotNull(saved.completedDate)
    }

    @Test
    fun onAdvance_isNoOp_forUnknownModule() = runTest(dispatcher) {
        val (vm, progressRepo) = buildViewModel(listOf(aModule("bourse", pageCount = 5)))
        subscribe(vm.listState)
        advanceUntilIdle()

        vm.onAdvance("does-not-exist", pageIndex = 0)
        advanceUntilIdle()

        assertTrue(progressRepo.saves.isEmpty())
    }
}
