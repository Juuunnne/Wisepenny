package com.wisepenny

import com.wisepenny.domain.model.Profile
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Non-regression test for ANO-001 (docs/anomalies/ANO-001-onboarding-flash.md):
 * the onboarding wizard used to flash at startup for an already-onboarded user
 * because the "profile read in flight" state was treated as "no profile".
 *
 * Locks the startup-routing contract of [startupScreen]:
 * while loading we route to [StartupScreen.LOADING] — never the wizard.
 */
class StartupScreenTest {

    private fun profile(onboardingCompleted: Boolean) = Profile(
        firstName = "Test",
        motivation = "",
        createdDate = LocalDate(2026, 1, 1),
        onboardingCompleted = onboardingCompleted,
        currency = "EUR",
        notificationsOptIn = false,
        bankLinked = false,
    )

    @Test
    fun whileLoading_routesToLoading_notOnboarding() {
        // The core of ANO-001: during the initial DB read we must show neither the
        // wizard nor the app, so an onboarded user never sees the onboarding flash.
        assertEquals(StartupScreen.LOADING, startupScreen(ProfileLoad.Loading))
    }

    @Test
    fun loadedOnboardedProfile_routesToApp() {
        assertEquals(
            StartupScreen.APP,
            startupScreen(ProfileLoad.Loaded(profile(onboardingCompleted = true))),
        )
    }

    @Test
    fun loadedNewUserProfile_routesToOnboarding() {
        assertEquals(
            StartupScreen.ONBOARDING,
            startupScreen(ProfileLoad.Loaded(profile(onboardingCompleted = false))),
        )
    }

    @Test
    fun loadedWithNoProfile_routesToOnboarding() {
        // Read completed, genuinely no profile yet -> new user -> wizard.
        assertEquals(
            StartupScreen.ONBOARDING,
            startupScreen(ProfileLoad.Loaded(profile = null)),
        )
    }
}
