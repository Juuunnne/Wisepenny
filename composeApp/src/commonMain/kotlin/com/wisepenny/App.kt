package com.wisepenny

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisepenny.data.seed.DataSeeder
import com.wisepenny.domain.model.Profile
import com.wisepenny.domain.repository.ProfileRepository
import com.wisepenny.presentation.navigation.AppNavHost
import com.wisepenny.presentation.onboarding.OnboardingRoot
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyTheme
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

/**
 * Result of the first profile read. [Loading] distinguishes "still reading the DB"
 * from "read completed, no profile yet" — the latter must show the wizard, the
 * former must show nothing (avoids a wizard flash for onboarded users).
 */
private sealed interface ProfileLoad {
    data object Loading : ProfileLoad
    data class Loaded(val profile: Profile?) : ProfileLoad
}

@Composable
fun App() {
    WisepennyTheme {
        val profileRepository = koinInject<ProfileRepository>()
        val load by profileRepository.observe()
            .map<Profile?, ProfileLoad> { ProfileLoad.Loaded(it) }
            .collectAsStateWithLifecycle(initialValue = ProfileLoad.Loading)

        when (val current = load) {
            ProfileLoad.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WisepennyColors.BackgroundPrimary),
            )
            is ProfileLoad.Loaded ->
                if (current.profile?.onboardingCompleted == true) {
                    // Already onboarded: no seeding here (owned by the wizard), just
                    // apply any auto-saves that have come due since the last open.
                    val seeder = koinInject<DataSeeder>()
                    LaunchedEffect(Unit) { seeder.onAppStart() }
                    AppNavHost()
                } else {
                    OnboardingRoot()
                }
        }
    }
}
