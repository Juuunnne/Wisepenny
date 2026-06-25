package com.wisepenny.data.content

import kotlinx.serialization.Serializable

/** Wire shape of the bundled `files/modules.json`. Mapped to the domain via ModuleMapper. */
@Serializable
data class ModuleDto(
    val id: String,
    val title: String,
    val description: String,
    val level: String,
    val pages: List<ModulePageDto>,
)

@Serializable
data class ModulePageDto(
    val heading: String,
    val body: String,
    val example: String? = null,
)
