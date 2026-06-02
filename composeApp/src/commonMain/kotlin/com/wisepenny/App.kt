package com.wisepenny

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.wisepenny.data.seed.DataSeeder
import com.wisepenny.presentation.navigation.AppNavHost
import com.wisepenny.presentation.theme.WisepennyTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    WisepennyTheme {
        // Seed demo data once at app start (independent of which tab opens first).
        val seeder = koinInject<DataSeeder>()
        LaunchedEffect(Unit) { seeder.onAppStart() }

        AppNavHost()
    }
}
