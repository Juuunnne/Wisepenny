package com.wisepenny.di

import com.wisepenny.data.local.DatabaseDriverFactory
import com.wisepenny.data.repository.ChallengeRepositoryImpl
import com.wisepenny.data.repository.ContributionRepositoryImpl
import com.wisepenny.data.repository.GoalRepositoryImpl
import com.wisepenny.data.seed.DataSeeder
import com.wisepenny.db.WisepennyDatabase
import com.wisepenny.domain.repository.ChallengeRepository
import com.wisepenny.domain.repository.ContributionRepository
import com.wisepenny.domain.repository.GoalRepository
import com.wisepenny.presentation.challenge.ChallengeViewModel
import com.wisepenny.presentation.dashboard.DashboardViewModel
import com.wisepenny.presentation.goal.GoalViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// The shared dependency graph. The platform DatabaseDriverFactory is supplied
// separately (see initKoin) because Android needs a Context and iOS does not.
val appModule = module {
    single { WisepennyDatabase(get<DatabaseDriverFactory>().create()) }
    single<ChallengeRepository> { ChallengeRepositoryImpl(get()) }
    single<GoalRepository> { GoalRepositoryImpl(get()) }
    single<ContributionRepository> { ContributionRepositoryImpl(get()) }
    single { DataSeeder(get(), get()) }
    viewModelOf(::ChallengeViewModel)
    viewModelOf(::GoalViewModel)
    viewModelOf(::DashboardViewModel)
}
