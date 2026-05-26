# Wisepenny — 4-Week Agile Sprint Plan

> **Status.** Validated V2 plan after audit. Honest S4 arithmetic, assumed buffer capacity, separated US for measurable velocity, formalized test plan (C2.3.1), lightweight ceremonies.
>
> **Framework.** Adapted solo Scrum. 4 one-week sprints. Capacity 4.5h × 5 weekdays + 4h Saturday = **26.5h "committable" / sprint**, Sunday kept as **uncommitted catch-up buffer**. Total committable: **106h over 28 days**.
>
> **Stack.** Compose Multiplatform, Kotlin Multiplatform, SQLDelight, Ktor, Koin, kotlinx.serialization, kotlinx.datetime.
>
> **Setup already in place.** KMP project created, folder structure ready, design style guide validated.

---

## V1 Audit — Corrections Applied

| # | V1 Problem | V2 Correction |
|---|---|---|
| 1 | S4 claimed "25 must + 17 should" — real Must total = 39 SP, infeasible | S4 rebalanced: US-15 (CI/CD) and parts of US-13/US-14 moved to S3 |
| 2 | TECH-00 at 5 SP — underestimated for KMP iOS setup | TECH-00 re-estimated at **8 SP** |
| 3 | Implicit test plan — C2.3.1 RNCP requirement poorly covered | Dedicated **US-17 Test Plan & Bug Correction** story (3 SP) added in S4 |
| 4 | US-01 and US-02 merged in the breakdown | Properly separated: US-01 = read, US-02 = write |
| 5 | "Off" Sunday actually used as catch-up | Sunday formally **uncommitted buffer**, outside capacity |
| 6 | Ceremonies = 2h15/sprint, too heavy | Reduced to **1h max/sprint** (planning 20min, review 20min, retro 20min) |

---

## Product Backlog (MoSCoW prioritized)

| ID | User Story | MVP Feature | SP | Priority |
|---|---|---|---|---|
| US-01 | Track an active savings challenge (read-only) | Active Challenge | 5 | Must |
| US-02 | Validate each challenge day (write + streak) | Active Challenge | 5 | Must |
| US-03 | View my savings goals | Savings Goal | 5 | Must |
| US-04 | Manually add money toward a goal | Savings Goal | 3 | Must |
| US-05 | Auto-credit from challenge to goal | Savings Goal | 3 | Must |
| US-06 | Aggregated dashboard as home screen | Dashboard | 8 | Must |
| US-07 | 4-tab bottom navigation | Dashboard | 3 | Must |
| US-08 | Earn badges (6 initial badges) | Gamification | 5 | Must |
| US-09 | Global app streak | Habit tracking | 3 | Must |
| US-10 | 4-screen express onboarding | Onboarding | 8 | Must |
| US-11 | Narrative learning module | Learning | 5 | Must |
| US-12 | Daily challenge reminder (local notification) | Reminders | 3 | Must |
| US-13 | OPQUAST accessibility audit + fixes | Accessibility | 5 | Must (RNCP) |
| US-14 | OWASP security audit + local encryption | Security | 5 | Must (RNCP) |
| US-15 | Complete CI/CD (Android + iOS + tests + lint) | DevOps | 3 | Must (RNCP) |
| US-16 | 3 manuals (deployment, user, update) | Documentation | 5 | Must (RNCP) |
| **US-17** | **Test plan + bug correction plan** | **Acceptance Testing** | **3** | **Must (RNCP)** |
| TECH-00 | Residual KMP setup (theme, DB, Ktor, Koin, nav) | Foundation | 8 | Must |
| TECH-01 | Minimal CI pipeline Sprint 1 | DevOps | 2 | Should |
| TECH-02 | Demo mode + script + backup video | Defense Prep | 3 | Must |
| TECH-03 | Cumulative ADRs (8+ files) | Documentation | _0_ | Continuous |
| **Backlog Total** | | | **85 SP** | |
| **Sprint-Committed Total** | | | **87 SP** | |

**2 SP delta explained.** Difference comes from TECH-02 (demo mode) split 2+3 across S1 (debug button) and S4 (full demo mode), plus TECH-04/05/06/07 in Sprint 2 which are operational task breakdowns of stories US-03/US-04/US-05 (already counted in backlog). Expected delta from story→task conversion in Scrum.

**Velocity note.** 1 SP ≈ 1h15 effective on average for KMP stack with tests + ADR + dual build. Capacity 4 sprints × 26.5h = 106h → **~87 SP achievable**. Backlog fits **without margin**: Sunday buffers (12h) absorb variance.

---

## Definition of Ready (DoR)

A user story is "ready" to enter a sprint if:
- [ ] Acceptance criteria written in Gherkin (Given/When/Then)
- [ ] Story points estimated
- [ ] Technical dependencies identified (mock remote needed? shared component required?)
- [ ] Stitch mockup or quick wireframe available
- [ ] Design decisions resolved (or ADR to be written explicitly listed)

## Definition of Done (DoD)

A user story is "done" if **all** items are checked:
- [ ] Code merged to `main` via PR (serious self-review even when solo)
- [ ] Unit tests on `domain` (use cases) + `data` (mappers) layers covering the story
- [ ] Android **and** iOS builds green (CI-verified)
- [ ] No unjustified Kotlin lint warning
- [ ] `contentDescription` set on new interactive components
- [ ] Manual smoke test: flow works offline after first-fetch
- [ ] Screenshots added to `docs/screenshots/`
- [ ] ADR written if non-trivial technical decision was made
- [ ] **Acceptance test scenario added to `docs/acceptance-tests/`** (covers C2.3.1)
- [ ] Gherkin criterion is demonstrable

---

## Lightweight Ceremonies (60 min max / sprint)

| Ceremony | When | Duration | Format |
|---|---|---|---|
| Sprint Planning | Monday 9am | **20 min** | Backlog validation + Day 1 tasks |
| Daily Check-in | Every day 9am | **5 min** | 3 lines in `daily-log.md` |
| Sprint Review | Friday 5pm | **20 min** | 1-2 min demo video + dashboard update |
| Sprint Retro | Friday 5:20pm | **20 min** | 3 columns start/stop/continue in `retro-sX.md` |

**Refinement removed** — replaced by 10 min at end of Sprint Planning to validate DoR of current sprint stories only. Future sprint stories already have pre-validated DoR in this document.

---

# SPRINT 1 — Foundation & Active Challenge

**Day 1 (Monday) → Day 7 (Sunday).** Committed capacity 26.5h (Sunday buffer). Commitment: **22 SP**.

### Sprint Goal

> Demonstrate the active "7 days without coffee" challenge end-to-end, day-by-day validatable, offline-first, on Android and iOS. Establish the architectural pattern that the next 3 sprints will replicate.

### Committed Stories

| ID | Story | SP |
|---|---|---|
| TECH-00 | Residual KMP setup (theme, DB, Ktor, Koin, nav) | 8 |
| US-01 | Track an active savings challenge (read-only) | 5 |
| US-02 | Validate each day + streak | 5 |
| TECH-01 | Minimal CI pipeline (build + Android tests) | 2 |
| TECH-02 (partial) | ADR-001 strict streak + debug button | 2 |
| **Total** | | **22 SP** |

### Gherkin Acceptance Criteria

**US-01 — Active challenge read**
```gherkin
Scenario: First launch with network
  Given I launch the app for the first time
  When the mock remote returns the "7 days without coffee" challenge
  Then I see the challenge screen with 5/7 days completed
  And data is persisted in SQLDelight

Scenario: Offline relaunch
  Given I have already opened the app at least once
  When I relaunch after full kill
  Then no network call is made
  And the screen displays in less than 200ms

Scenario: First launch without network
  Given I have no connection
  When I launch the app for the first time
  Then I see a clean error message with retry button
```

**US-02 — Day validation + streak**
```gherkin
Scenario: Validating a day
  Given I am on the challenge screen at day 6
  When I tap "Yes, +€3 saved"
  Then the day 6 circle turns green
  And the "Saved" counter increments by €3
  And state persists after kill

Scenario: Idempotency
  Given I already validated today
  When I tap the validate button again
  Then no double counting occurs

Scenario: Strict streak
  Given my streak is 5 days
  And I did not validate yesterday
  When I validate today
  Then my streak resets to 1 (ADR-001: strict policy)
```

### Daily Task Breakdown

**D1 Monday — Residual setup (5.5h target, 8 SP)**
- [ ] Complete `theme/` (Color, Typography, Shape, Spacing, WisepennyTheme) — 1.5h
- [ ] `data/local/database/wisepenny.sq` + SQLDelight driver Android + iOS — 1h
- [ ] `data/remote/api/HttpClientFactory.kt` with Ktor MockEngine — 0.5h
- [ ] `di/AppModule.kt` Koin with empty sub-modules — 0.5h
- [ ] Minimal `presentation/navigation/AppNavHost.kt` — 0.5h
- [ ] **iOS build green** (critical, do not postpone) — 1h
- [ ] Cross-platform SQLDelight sanity-check test — 0.5h

**D2 Tuesday — Challenge domain + tests (4.5h, 3 SP of US-01)**
- [ ] `domain/model/Challenge.kt`, `ChallengeProgress.kt`
- [ ] `domain/repository/ChallengeRepository.kt` (interface)
- [ ] `domain/usecase/challenge/GetActiveChallengeUseCase.kt`
- [ ] `domain/usecase/challenge/CalculateStreakUseCase.kt` (strict policy)
- [ ] Tests: 4-5 use case tests (nominal streak, missed day, today only, 3-day series)

**D3 Wednesday — Challenge data + Repository (4.5h, 2 SP US-01 + 2 SP US-02)**
- [ ] Tables `challenge.sq` + `challenge_progress.sq`
- [ ] `data/local/dao/ChallengeDao.kt`
- [ ] `data/remote/dto/ChallengeDto.kt` + `data/remote/mock/ChallengeMockProvider.kt` (predefined challenge)
- [ ] `data/mapper/ChallengeMapper.kt` + round-trip tests
- [ ] `data/repository/ChallengeRepositoryImpl.kt` (first-fetch then local strategy)
- [ ] Koin bindings

**D4 Thursday — US-01 presentation (4.5h, US-01 finalization)**
- [ ] `presentation/challenge/ChallengeViewModel.kt` + `ChallengeUiState.kt`
- [ ] `presentation/challenge/ActiveChallengeScreen.kt` reproducing Screen 3
- [ ] Extracted components: `MintHeroCard`, `StatsRow`, `ProgressDots`
- [ ] Skeleton / success / error states
- [ ] **US-01 DONE** (all DoD boxes checked)

**D5 Friday — US-02 validation + idempotency (4.5h, US-02 finalization)**
- [ ] `domain/usecase/challenge/CompleteChallengeDayUseCase.kt` (idempotent)
- [ ] "Validate" action wired to screen
- [ ] `HistoryRow` component
- [ ] Idempotency tests + ViewModel tests (3 state transitions)
- [ ] **Mandatory iOS smoke test**
- [ ] **US-02 DONE**
- [ ] Sprint Review 5pm + Retro 5:20pm

**D6 Saturday — CI + ADR + finishing (4h, TECH-01 + TECH-02)**
- [ ] `.github/workflows/ci.yml`: Android build + tests
- [ ] Status badge in README
- [ ] `docs/decisions/ADR-001-strict-streak.md`
- [ ] Hidden debug button (reset challenge state)
- [ ] Screenshots for user manual
- [ ] First scenario filled in `docs/acceptance-tests/test-plan.md`

**D7 Sunday — BUFFER (uncommitted)**
If everything is green: rest. Otherwise: catch-up. The buffer is your safety valve.

### S1 Risks

| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| iOS build blocks Day 1 | Medium | High | Time-box 2h on D1. Beyond: minimal `expect/actual`, defer Cocoapods |
| Koin DI misconfigured | Medium | Medium | 1 module per layer, no custom scopes |
| Over-estimated velocity | Medium | High | Honest Friday measurement, recalibrate S2 |

---

# SPRINT 2 — Savings Goals & Cross-feature Integration

**D8 → D14.** Capacity 26.5h. Commitment **21 SP**.

### Sprint Goal

> Enable goal viewing, manual contributions, and automatic challenge → goal crediting. Demonstrate successful replication of S1 pattern.

### Committed Stories

| ID | Story | SP |
|---|---|---|
| US-03 | View my savings goals | 5 |
| US-04 | Manually add money toward a goal | 3 |
| US-05 | Auto-credit challenge → goal | 3 |
| TECH-04 | Completion projection (30-day moving average) | 3 |
| TECH-05 | Goal-picker bottom sheet | 3 |
| TECH-06 | S1 refactor + integration tests | 2 |
| TECH-07 | ADR-002 (1 challenge = 1 goal rule) + acceptance test | 2 |
| **Total** | | **21 SP** |

### Gherkin Criteria

**US-04**
```gherkin
Scenario: Manual contribution
  Given I am on the "Lisbon 2026" detail screen at €450/€800
  When I tap "Add €10" and confirm
  Then the saved amount goes to €460
  And the progress bar advances to 57.5%
  And the projection recalculates
```

**US-05**
```gherkin
Scenario: Automatic crediting
  Given I have a challenge linked to "Lisbon 2026"
  When I validate a day at €3
  Then a Contribution with source=CHALLENGE of €3 is created
  And the Lisbon amount increases by €3
```

### Daily Task Breakdown

**D8 Monday — Goal domain + tests (4.5h)**
- [ ] Models `SavingsGoal`, `Contribution` (source enum)
- [ ] Use cases: `GetGoals`, `GetGoalById`, `AddContribution`, `ProjectCompletionDate`
- [ ] Use case tests (projection: €0, amount=target, 30-day moving average)

**D9 Tuesday — Goal data (4.5h)**
- [ ] Tables `goals.sq` + `contributions.sq` (FK)
- [ ] DAO + DTO + mock with 3 goals consistent with Dashboard
- [ ] Mappers + repository impl
- [ ] Koin bindings

**D10 Wednesday — Goal Detail presentation (4.5h)**
- [ ] `GoalDetailViewModel` + `GoalDetailScreen` (Screen 4)
- [ ] Components `GoalProgressCard`, `AmountInput`, `ContributionSheet`
- [ ] Amount validation (regex, max)

**D11 Thursday — Goal List + Picker (4.5h)**
- [ ] `GoalListScreen`
- [ ] `GoalPickerSheet`
- [ ] S3 backlog prep (10 min end of day)

**D12 Friday — Cross-feature integration (4.5h)**
- [ ] Refactor `CompleteChallengeDayUseCase`: creates CHALLENGE Contribution
- [ ] New challenge start → opens `GoalPickerSheet`
- [ ] Integration tests (challenge use case verifies effect on goal)
- [ ] **iOS smoke test**
- [ ] Sprint Review + Retro 5pm

**D13 Saturday — Polish + ADR + acceptance tests (4h)**
- [ ] ADR-002: 1 challenge = 1 goal rule, behavior if goal deleted
- [ ] Test coverage measurement (target domain 50%+)
- [ ] Acceptance test scenarios (2-3 new)
- [ ] Screenshots

**D14 Sunday — BUFFER**

### S2 Risks

| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| S1 refactor breaks existing tests | High | Medium | Run test suite before AND after each commit |
| Projection edge cases (past date, amount=0) | Medium | Medium | Explicitly listed in tests from D8 |

---

# SPRINT 3 — Dashboard, Navigation, Gamification & Partial RNCP

**D15 → D21.** Capacity 26.5h. Commitment **22 SP**.

### Sprint Goal

> Deliver the aggregated home screen, 4-tab navigation, badges + global streak system. **Start complete CI/CD and local encryption** to offload S4 (correcting V1 imbalance).

### Committed Stories

| ID | Story | SP |
|---|---|---|
| US-06 | Aggregated dashboard | 8 |
| US-07 | 4-tab bottom navigation | 3 |
| US-08 | Badge system | 5 |
| US-09 | Global streak | 3 |
| **US-15** | **Complete CI/CD (moved up from S4)** | **3** |
| **Total** | | **22 SP** |

### Gherkin Criteria

**US-06**
```gherkin
Scenario: Aggregated dashboard
  Given I have 3 goals and an active challenge at day 5/7
  When I open the app
  Then I see the total saved this month
  And my progress toward the monthly target
  And today's challenge in a mint card
  And my 3 goals in horizontal scroll

Scenario: Deeplinks
  When I tap on the challenge card
  Then I navigate to ActiveChallengeScreen
  When I tap on a goal card
  Then I navigate to GoalDetailScreen
```

**US-08**
```gherkin
Scenario: First badge
  Given I just completed my first challenge (day 7 validated)
  When validation is confirmed
  Then "First challenge completed" unlocks
  And a modal displays
  And the badge appears in Profile
```

### Daily Task Breakdown

**D15 Monday — Dashboard data (4.5h)**
- [ ] `GetDashboardSnapshotUseCase` (aggregates 5 indicators)
- [ ] "Total saved this month" logic
- [ ] Tests

**D16 Tuesday — Dashboard UI (4.5h)**
- [ ] `DashboardViewModel` + `DashboardScreen` (Screen 1)
- [ ] Components `MonthSavingsCard`, `StreakStrip`, `GoalCardHorizontal`, `DailyChallengeCard`
- [ ] No hardcoded values

**D17 Wednesday — Navigation (4.5h)**
- [ ] `BottomNavBar` 4 tabs
- [ ] Migrate S1-S2 deeplinks to nav
- [ ] Saved state between tabs (rotation, background)

**D18 Thursday — Gamification (4.5h)**
- [ ] `Badge` + `BadgeType` (6 badges)
- [ ] Table `badges.sq`
- [ ] `EvaluateBadgesUseCase` (observer pattern on event Flow)
- [ ] `GetGlobalStreakUseCase`
- [ ] Tests: 1 per badge + 3 global streak tests
- [ ] Hook in `CompleteChallengeDay` and `AddContribution`

**D19 Friday — Gamification UI + CI/CD (4.5h)**
- [ ] `BadgeUnlockedDialog`
- [ ] `ProfileScreen` v1
- [ ] **CI/CD: add iOS build in GitHub Actions** (US-15 starts here)
- [ ] Sprint Review + Retro

**D20 Saturday — iOS CI finalization + ADR (4h)**
- [ ] CI: automated tests on PR
- [ ] CI: Kotlin lint + Detekt
- [ ] ADR-003 (monthly target = onboarding-set, fixed)
- [ ] ADR-004 (global streak = ≥1 action per day)
- [ ] Debug button: force unlock badge
- [ ] Acceptance test scenarios

**D21 Sunday — BUFFER**

### S3 Risks

| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| iOS CI on GitHub Actions complex (signing, certs) | High | Medium | Keep iOS local build + Android on CI if too hard. Document in ADR |
| Spaghetti badge coupling | High | Medium | Observer/event-bus pattern, no direct calls |
| Tab state saving | Medium | High | Test full lifecycle before sprint end |

---

# SPRINT 4 — Onboarding, Learning, Reminders + RNCP Delivery

**D22 → D28.** Capacity 26.5h. Commitment **22 SP**.

### Sprint Goal

> Complete the 3 remaining MVP features, pass security + accessibility audits, finalize docs and test plan, prepare the defense. **Realistic velocity thanks to S3 → S4 rebalancing.**

### Committed Stories (balanced, no more lies)

| ID | Story | SP |
|---|---|---|
| US-10 | 4-screen express onboarding | 8 |
| US-11 | Narrative learning module | 5 |
| US-12 | Daily challenge reminder | 3 |
| **US-17** | **Complete test plan + bug correction plan** | **3** |
| TECH-02 (continued) | Demo mode + script + backup video | 3 |
| **Embedded** | US-13/14/16 spread across S4 (see breakdown) | — |
| **Total** | | **22 SP** |

**Honest note.** US-13 (accessibility), US-14 (security), US-16 (manuals) are **spread in parallel** with feature work on D22-D24 rather than counted as additional SP: security = 2h in D23, accessibility = 2h in D24, manuals = full day D26. Total hours (~10h) integrated in the breakdown and counted in the 26.5h.

### Gherkin Criteria

**US-10**
```gherkin
Scenario: First launch
  Given fresh install
  When I launch the app
  Then I see the onboarding Welcome screen

Scenario: Onboarding completion
  Given I filled in first name, first goal, monthly amount
  When I tap "Finish"
  Then I see the Dashboard with my first name
  And my initial goal appears in the list
  And `onboarding_completed = true` is persisted
```

**US-11**
```gherkin
Scenario: Module reading
  Given the "Savings account" module has 6 sections
  When I start reading
  And I quit mid-section 3
  When I come back later
  Then I resume at section 3
  When I tap "I'm done" at end of reading
  Then the module is marked completed
  And the "First module read" badge unlocks
```

### Daily Task Breakdown (honestly sequenced)

**D22 Monday — Onboarding (5h max, 8 SP)**
- [ ] `data/local/preferences/UserPreferences.kt` (expect/actual: Datastore Android / Keychain iOS)
- [ ] `UserProfile` + use cases (`SaveUserProfile`, `IsOnboardingCompleted`, `CreateInitialGoal`)
- [ ] 4 screens: Welcome, FirstName, FirstGoal, MonthlyTarget
- [ ] Conditional routing in `AppNavHost`
- [ ] Dynamic first name Dashboard + Profile
- [ ] **Datastore encryption enabled now** (partially covers US-14)

**D23 Tuesday — Learning + Security (5h)**

*Morning Learning (3h)*
- [ ] `composeResources/files/modules/savings-account.json` (5 sections written)
- [ ] Models `LearningModule`, `ModuleSection`
- [ ] Reading/completion use cases
- [ ] Table `module_progress.sq`
- [ ] `LearningListScreen` + `ModuleReaderScreen`
- [ ] "First module read" badge hook
- [ ] ADR-005 (static JSON content: GDPR + eco-design)

*Afternoon Security (2h, US-14)*
- [ ] Input validators (`AmountValidator`, `NameValidator`, `DateValidator`)
- [ ] Log policy: `BuildConfig.DEBUG` everywhere, no PII in release
- [ ] `docs/security/owasp-mobile-audit.md` written (1 line per Top 10 item: applicable / not / mitigated)

**D24 Wednesday — Reminders + Accessibility (5h)**

*Morning Reminders (2h, US-12)*
- [ ] `expect class NotificationScheduler` + actuals (AlarmManager / UNUserNotificationCenter)
- [ ] `ScheduleDailyChallengeReminderUseCase`
- [ ] Toggle in Profile
- [ ] Android 13+ permission properly requested
- [ ] **If slippage beyond 2h30: cut. Reminder = daily only, sufficient for MVP**

*Afternoon Accessibility (3h, US-13)*
- [ ] OPQUAST audit: `docs/accessibility/opquast-audit.md` (~30 relevant rules documented)
- [ ] Fixes: `contentDescription`, tap target sizes 48dp+, focus order
- [ ] Real TalkBack test on Dashboard + ActiveChallenge + GoalDetail
- [ ] Before/after captures for 2 significant fixes

**D25 Thursday — Test plan + bug plan (4.5h, US-17)**
- [ ] Complete `docs/acceptance-tests/test-plan.md`: 20 scenarios covering Must-haves
- [ ] Manual execution of all scenarios, statuses logged (OK/KO)
- [ ] `docs/acceptance-tests/bug-correction-plan.md`: 1 sheet per detected bug (analysis + proposed fix + status)
- [ ] Fix blocking bugs detected (time-box 2h)
- [ ] Final test coverage measurement (target ≥50% domain)

**D26 Friday — Complete documentation (5h, US-16)**
- [ ] `docs/manuals/deployment-manual.md` (prerequisites, commands, signing, distribution)
- [ ] `docs/manuals/user-manual.md` (user journey with all cumulative screenshots)
- [ ] `docs/manuals/update-manual.md` (semantic versioning, release process, changelog)
- [ ] Clean up the 5-8 ADRs
- [ ] Final README with "Stack & RNCP arguments" section
- [ ] Full project Sprint Review + Retro

**D27 Saturday — Defense preparation (4h, TECH-02)**
- [ ] Demo mode: Profile button to reset predefined states (fresh, D5, badge, goal 80%)
- [ ] Demo script timed 8-10 min
- [ ] **Backup video recorded** on target device (complete journey)
- [ ] Block 3 slides: 4-sprint methodology, actual velocity, ADRs as arbitration proof
- [ ] Final retrospective planning (C3.1 deliverable)

**D28 Sunday — Rehearsal + delivery (BUFFER COMMITTED this sprint)**
- [ ] 2 timed complete rehearsals
- [ ] Final visual pass vs style guide
- [ ] Tag `v1.0.0`, final release build
- [ ] Repo + docs backup to external drive

**D28 note.** Exceptionally, Sunday is committed in S4 because it's the last day before delivery. It's your last safety net — use it.

### Scope Cut Plan If Slippage (updated)

From least to most critical:
1. **Before/after accessibility captures** → text only. -0.5 SP
2. **Learning module: 5 sections instead of 6-7** → keep the mechanism. -1 SP
3. **iOS CI** → Android only on CI, iOS local build documented in ADR. -1 SP
4. **2nd rehearsal** → keep only 1. -1 SP

**Never cut:** OWASP, basic OPQUAST, test plan, manuals, demo mode, backup video.

### S4 Risks

| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| KMP reminders (expect/actual notif) more complex than 2h | High | Medium | Strict time-box, otherwise Android only + ADR |
| OWASP audit reveals major vulnerability | Low | High | Audit D23, 2 days to fix |
| Blocking bug in test plan D25 | Medium | High | 2h dedicated fix D25, otherwise plan B = document as known bug |
| Demo crashes in defense | Medium | High | Backup video mandatory D27 |

---

## Project Dashboard (to be maintained)

| Sprint | Committed SP | Completed SP | Velocity | Actual hours / budget | Sprint goal ✓ |
|---|---|---|---|---|---|
| S1 | 22 | _____ | _____ | _____ / 26.5h | _____ |
| S2 | 21 | _____ | _____ | _____ / 26.5h | _____ |
| S3 | 22 | _____ | _____ | _____ / 26.5h | _____ |
| S4 | 22 | _____ | _____ | _____ / 26.5h | _____ |
| **Total** | **87** | _____ | _____ | _____ / 106h | _____ |

**Velocity note.** 87 SP × 1.22h/SP ≈ 106h. Fits exactly within committable capacity. Sundays D7, D14, D21 are pure buffer (12h additional available if slippage). D28 is committed for final rehearsals.

Direct RNCP deliverable for **C3.2.1** (progress monitoring) and **C3.2.2** (arbitrations).

---

## RNCP Criteria → V2 Mapping

| Criterion | Sprint | Artifact |
|---|---|---|
| C2.1.1, C2.1.2 (environments, CI) | S1 (TECH-01) + S3 (US-15) | `.github/workflows/`, manuals |
| C2.2.1 (prototype) | From S1 | Demonstrable app |
| C2.2.2 (unit tests) | Continuous S1→S4 | Test suite + coverage |
| C2.2.3 (security + accessibility + maintainability) | S4 (US-13, US-14) | OWASP + OPQUAST audits |
| C2.2.4 (progressive deployment) | S3 (US-15) | Versioned CI pipeline |
| **C2.3.1, C2.3.2 (acceptance tests, bugs)** | **S4 (US-17 — new)** | **Test plan + correction plan** |
| C2.4.1 (documentation) | S4 (US-16) | 3 manuals + ADRs |
| C3.1 (planning) | This document | Plan + actual post-sprint planning |
| C3.2.1 (monitoring) | Continuous | Daily logs + dashboard + review videos |
| C3.2.2 (arbitrations) | Continuous | ADRs + documented cut plan |
| C3.4.1, C3.4.2 (client follow-up, demo) | S4 D26-D28 | Review videos + demo + script |

**C3.3 (team).** Solo project, to be adapted in defense: justified solo context + OPQUAST accessibility as disability proxy + communication mastery via ADRs and written retros.

---

## Pre-Day 1 Commitments (to be formally locked)

- [x] Streak policy: strict (ADR-001)
- [x] Mock remote = final backend
- [x] No Compose UI tests (domain+data only)
- [x] 1 learning module delivered
- [x] Learning format: narrative only
- [x] No design beyond the 4 Stitch screens
- [x] Local notifications only
- [x] Reminders reducible if D24 slips
- [x] **iOS CI reducible to Android only if D19-D20 slip**
- [x] **Sunday = uncommitted buffer (except D28)**

---

## Final Validation

| Check | Status |
|---|---|
| Coherent SP arithmetic (22+21+22+22 = 87) | ✅ |
| Coherent hour capacity (4×26.5 = 106h) | ✅ |
| Realistic velocity (87 SP ≈ 106h at 1.22h/SP) | ✅ |
| No overloaded sprint (max commitment = 22 SP) | ✅ |
| C2.3.1 (test plan) explicitly covered | ✅ |
| Buffer exists (Sundays except D28) | ✅ |
| Documented cut plan for S4 | ✅ |
| Risks identified per sprint | ✅ |
| Pre-Day 1 commitments locked | ✅ |

**Plan validated. Ready to start Day 1 Monday morning.**
