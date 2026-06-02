package com.wisepenny.presentation.navigation

import kotlinx.serialization.Serializable

// Type-safe navigation routes. The kotlinx-serialization plugin is already
// applied, so navigation-compose can serialize these to/from the back stack.

@Serializable
object Accueil

@Serializable
object Apprendre

@Serializable
object Objectifs

@Serializable
object Profil

@Serializable
data class GoalDetailRoute(val goalId: Long)

@Serializable
data class ChallengeDetailRoute(val challengeId: Long)
