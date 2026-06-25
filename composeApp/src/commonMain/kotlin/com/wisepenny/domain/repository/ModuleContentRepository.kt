package com.wisepenny.domain.repository

import com.wisepenny.domain.model.LearningModule

interface ModuleContentRepository {

    /** Loads the static module catalogue (bundled JSON), in display order. */
    suspend fun loadModules(): List<LearningModule>
}
