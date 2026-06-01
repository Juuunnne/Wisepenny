package com.wisepenny.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.mp.KoinPlatform

// Called once from each platform entry point. The guard makes it safe to call
// again (e.g. Android recreates MainActivity on rotation) without throwing
// KoinApplicationAlreadyStartedException.
fun initKoin(platformModule: Module) {
    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        modules(appModule, platformModule)
    }
}
