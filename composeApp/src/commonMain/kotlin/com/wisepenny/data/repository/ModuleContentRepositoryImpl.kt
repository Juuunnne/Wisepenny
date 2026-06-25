package com.wisepenny.data.repository

import com.wisepenny.data.content.ModuleDto
import com.wisepenny.data.mapper.toDomain
import com.wisepenny.domain.model.LearningModule
import com.wisepenny.domain.repository.ModuleContentRepository
import com.wisepenny.resources.Res
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Serves the static learning catalogue from a bundled JSON file
 * (composeResources/files/modules.json). Parsed once and cached — the content
 * never changes at runtime, so there is no Flow here (progress is the only
 * mutable part; see [com.wisepenny.domain.repository.ModuleProgressRepository]).
 */
class ModuleContentRepositoryImpl : ModuleContentRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private var cache: List<LearningModule>? = null

    override suspend fun loadModules(): List<LearningModule> =
        cache ?: run {
            val bytes = Res.readBytes("files/modules.json")
            json.decodeFromString<List<ModuleDto>>(bytes.decodeToString())
                .map { it.toDomain() }
                .also { cache = it }
        }
}
