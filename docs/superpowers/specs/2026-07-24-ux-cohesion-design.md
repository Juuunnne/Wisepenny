# UX Cohesion Pass — Design

- **Date:** 2026-07-24
- **Status:** Approved (pending final spec review)
- **Scope:** Client (`composeApp`) only. No backend/server changes.
- **Related, deferred:** A real gamification reward system (points → store discounts) is a
  *separate* effort to be designed after this pass. This spec deliberately does not build it.

---

## 1. Why

A manual test surfaced that the app doesn't feel like one cohesive product, and several
controls lead nowhere. The sweep found four concrete causes:

1. **Screens are structured two different ways.** The Dashboard is the only screen using a
   raw `Column` + `statusBarsPadding()`; every other tab and detail screen uses a `Scaffold`.
   Insets, background, and outer padding therefore differ screen-to-screen, and each screen
   hand-rolls its own header.
2. **Design tokens exist but are unused.** `WisepennyShapes` (radius scale 12/16/20/24/28) is
   defined in `theme/Shape.kt` and referenced only by the two theme files. Every screen instead
   hardcodes `RoundedCornerShape(n.dp)` inline (10× in Dashboard, 12× in Goal detail, 7× in
   Onboarding…), which is the main reason cards look subtly different everywhere. Cards are also
   built inconsistently — sometimes Material3 `Card`, sometimes raw `Column().clip().background()`.
3. **Mixed color access.** Most surfaces read `WisepennyColors.*` directly; a few read
   `MaterialTheme.colorScheme.*`. Two conventions for the same palette.
4. **Dead ends.** Six controls did nothing: add-goal (`+`), edit-goal, "see all challenges",
   share, the promo CTA ("Relever le défi"), and the notification bell.

## 2. Goals / Non-goals

**Goals**
- Every screen is built from the same small set of shared primitives and design tokens, so the
  app reads as one product.
- No control is a dead end: each is either wired to a working feature or removed.

**Non-goals (out of scope)**
- The reward/discount system, XP economy, and making the streak "real" — deferred to a separate
  design. The hardcoded streak (5/7) and cosmetic XP string are left as-is here.
- Push notifications / OS scheduling.
- Any server, API, or persistence-schema change beyond the two small additions in §5.

## 3. Approach: foundation first, then features

Build the shared cohesion primitives and refactor the existing screens onto them **first** — a
standalone, demoable milestone that directly delivers "cohesive." Then build the remaining
features on top of those primitives, so each is consistent the first time rather than retrofitted.

---

## 4. Phase 1 — Cohesion foundation

### 4.1 Shared primitives (new, in `presentation/components/`)

- **`WisepennyScaffold`** — the wrapper every **tab** screen sits in. Owns background color
  (`BackgroundPrimary`), status-bar insets, and consistent outer padding (`Spacing.xl` horizontal).
  Exposes a `header` slot and a scrollable content slot. Replaces the Dashboard's bespoke
  `Column` + `statusBarsPadding()` and the other screens' ad-hoc `Scaffold`s.
- **`WisepennyTopBar`** — for **detail** screens (goal, challenge): a back arrow + title, styled
  from tokens, so back-navigation chrome is identical everywhere.
- **`WisepennyScreenHeader`** — a title with an optional `leading` slot (Dashboard avatar) and an
  optional `trailing` slot. (The Dashboard's trailing bell is removed — see §6.)
- **`WisepennyCard`** — one card with three variants matching current usage:
  - `Elevated` → `SurfaceElevated` (default; most cards)
  - `Light` → `SurfaceLight` with dark text (priority goal, monthly-savings card)
  - `Accent` → `AccentMint` (promo card)
  Each takes a shape token (default `WisepennyShapes.large` = 24dp) and a content slot.

### 4.2 Token adoption (the primary visual fix)

- Replace every inline `RoundedCornerShape(n.dp)` with the corresponding `WisepennyShapes` token.
- Standardize on **`WisepennyColors.*` accessed directly** as the single color convention; remove
  the stray `MaterialTheme.colorScheme.*` reads. Rationale: the palette has more tokens than the
  Material scheme slots, and it's already the source of truth in the majority of the code.

### 4.3 Screens refactored onto the primitives

Dashboard, Profil, Apprendre (list), Objectifs (list), Goal detail, Challenge detail, Onboarding.
The full-bleed **Module reader** keeps its no-chrome layout but adopts the shape/color tokens.

### 4.4 Verification

Phase 1 changes **no business logic**, so existing unit tests stay green. Visual correctness is
verified via one `@Preview` per refactored screen (keep/add as needed) plus a manual run. Builds
are run by the developer.

---

## 5. Phase 2 — Wiring the dead ends

Each is a small vertical slice built on the Phase 1 primitives. Five features (the bell is removed,
not wired).

1. **Add-goal (`+`).** A `GoalFormScreen` (name, target amount, icon, optional target date,
   priority toggle) calling the existing `GoalRepository.create(...)`. Already backed by the data
   layer.
2. **Edit-goal.** The same `GoalFormScreen` in an "edit" mode, pre-filled. Requires new plumbing:
   `GoalRepository.update(...)` + a SQLDelight update query + mapper. Isolated addition.
3. **Challenge list ("see all challenges").** A `ChallengeListScreen` showing **all** challenges
   with **filter chips** at the top: **Tous / Actifs / Terminés**. "Terminé" is derived
   (`completedDays >= totalDays`). Requires one new `ChallengeRepository.observeAll()` query;
   filtering happens in the ViewModel.
4. **Share.** An `expect/actual` share shim — Android `Intent.ACTION_SEND`, iOS
   `UIActivityViewController` — wired to the challenge screen's share button with a simple message
   (e.g. "J'ai économisé X € avec Wisepenny 💸").
5. **"Défi du jour" (promo CTA).** A bottom sheet proposing today's micro-challenge; accepting
   starts it via `ChallengeRepository.create(...)`, replacing the dead promo button with a real loop.

## 6. Removed

- **Notification bell** — removed from the Dashboard header. No notification data model is
  invented; a real reminders/notification system is out of scope for this pass.

## 7. Data-layer changes (the only non-UI changes)

- `GoalRepository.update(...)` + SQLDelight update statement + mapper support (for edit-goal).
- `ChallengeRepository.observeAll()` + SQLDelight query (for the challenge list).

Both mirror existing patterns (`create`, `observeActiveChallenges`) and add no new tables/columns.

## 8. Sequencing into implementation plans

This doc captures the whole vision, but the work lands incrementally:

- **Plan 1 — Phase 1** (foundation + refactor of existing screens). The standalone cohesion
  milestone; ship and demo before touching features.
- **Plan 2+ — Phase 2 features**, one or two at a time (add/edit goal → challenge list → share →
  défi du jour), each built on the Phase 1 primitives.

## 9. Testing summary

- Phase 1: no logic change → existing tests stay green; previews + manual run for visuals.
- Phase 2: unit-test the new repository methods (`update`, `observeAll`) and the challenge-filter
  logic in the ViewModel, following the existing `commonTest` patterns. Form validation logic gets
  unit coverage. Platform share shim is verified manually per platform.
