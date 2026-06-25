package com.wisepenny.presentation.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.domain.model.LearningModule
import com.wisepenny.domain.model.ModuleProgress
import com.wisepenny.domain.repository.ModuleContentRepository
import com.wisepenny.domain.repository.ModuleProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Drives the Apprendre tab. The static catalogue (bundled JSON) is loaded once
 * and combined with the saved progress Flow, so the list and reader stay in sync
 * with the DB — the single source of truth shared with any other live instance.
 */
class LearningViewModel(
    private val contentRepository: ModuleContentRepository,
    private val progressRepository: ModuleProgressRepository,
) : ViewModel() {

    private val catalog = MutableStateFlow<List<LearningModule>>(emptyList())
    private val selectedModuleId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch { catalog.value = contentRepository.loadModules() }
    }

    val listState: StateFlow<LearningListUiState> =
        combine(catalog, progressRepository.observeAll()) { modules, progress ->
            buildListState(modules, progress.associateBy { it.moduleId })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LearningListUiState(emptyList()),
        )

    /**
     * Reader state for the [selectedModuleId]. Resumes at the saved page (or page 0
     * when re-reading a completed module); null until the catalogue loads or if no
     * module is selected.
     */
    val readerState: StateFlow<ModuleReaderUiState?> =
        combine(selectedModuleId, catalog, progressRepository.observeAll()) { id, modules, progress ->
            val module = modules.firstOrNull { it.id == id } ?: return@combine null
            val saved = progress.firstOrNull { it.moduleId == id }
            val lastIndex = module.pages.lastIndex.coerceAtLeast(0)
            val startIndex =
                if (saved?.completed == true) 0 else (saved?.pagesRead ?: 0).coerceIn(0, lastIndex)
            ModuleReaderUiState(
                moduleId = module.id,
                title = module.title,
                pages = module.pages.map { ModuleReaderPage(it.heading, it.body, it.example) },
                startIndex = startIndex,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun selectModule(id: String) {
        selectedModuleId.value = id
    }

    /** Persists that the user has read through page [pageIndex] (0-based) of [moduleId]. */
    fun onAdvance(moduleId: String, pageIndex: Int) {
        val module = catalog.value.firstOrNull { it.id == moduleId } ?: return
        val pagesRead = (pageIndex + 1).coerceAtMost(module.pages.size)
        val completed = pagesRead >= module.pages.size
        viewModelScope.launch {
            progressRepository.saveProgress(
                moduleId = moduleId,
                pagesRead = pagesRead,
                completed = completed,
                completedDate = if (completed) today() else null,
            )
        }
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private companion object {
        /** Pure mapping: catalogue + progress → list items with linear unlock state. */
        fun buildListState(
            modules: List<LearningModule>,
            progressById: Map<String, ModuleProgress>,
        ): LearningListUiState {
            val items = modules.mapIndexed { index, module ->
                val progress = progressById[module.id]
                val pagesRead = progress?.pagesRead ?: 0
                val completed = progress?.completed == true
                val total = module.pages.size.coerceAtLeast(1)
                val ratio = (pagesRead.toFloat() / total).coerceIn(0f, 1f)
                // Linear unlock: the first module is always open; the rest open once
                // the previous module is completed.
                val prevCompleted =
                    index == 0 || progressById[modules[index - 1].id]?.completed == true
                val status = when {
                    completed -> ModuleStatus.COMPLETED
                    prevCompleted -> ModuleStatus.ACTIVE
                    else -> ModuleStatus.LOCKED
                }
                ModuleListItem(
                    id = module.id,
                    title = module.title,
                    description = module.description,
                    levelLabel = module.level.label,
                    status = status,
                    progress = if (completed) 1f else ratio,
                    progressPercent = (ratio * 100).toInt(),
                )
            }
            return LearningListUiState(items)
        }
    }
}
