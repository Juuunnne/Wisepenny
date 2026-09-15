# Wisepenny — Sprint Plan & Execution Record

> **Status.** Two-part document. **Part 1** is the original 4-week V2 sprint plan, validated and committed before Day 1 (2026-05-26) — kept unchanged below for traceability (RNCP C3.1, C3.2.2). **Part 2**, added 2026-09-15, is the **execution record**: what actually shipped, the scope/timeline arbitrations made along the way, and what is still open. Read the Execution Summary first — it tells you which parts of Part 1 are historical intent vs. current reality.
>
> **Framework (as planned).** Adapted solo Scrum. 4 one-week sprints. Capacity 4.5h × 5 weekdays + 4h Saturday = **26.5h "committable" / sprint**, Sunday kept as **uncommitted catch-up buffer**. Total committable: **106h over 28 days**. *(Not how it actually went — see Execution Summary.)*
>
> **Stack (as planned).** Compose Multiplatform, Kotlin Multiplatform, SQLDelight, Ktor, Koin, kotlinx.serialization, kotlinx.datetime.
>
> **Stack (as actually built).** All of the above, plus a real backend: **Ktor Server, Exposed ORM, PostgreSQL, JWT auth, BCrypt**, tested end-to-end with **Testcontainers**; **Detekt + Ktlint** static analysis; **Dependabot** dependency watch.
>
> **Setup already in place (Day 1).** KMP project created, folder structure ready, design style guide validated.

---

## Execution Summary (added 2026-09-15)

The plan below was validated and locked before Day 1. Here is what actually happened over the ~16 weeks since.

- **Timeline.** The project has run **2026-05-26 → 2026-09-15 (112 days, ~16 weeks)** and is still open — roughly **4× the 28-day box** committed in the V2 plan. There was no formal daily-log / retro cadence as specified in the Ceremonies section; `CHANGELOG.md` and the Git history serve as the de facto record instead.
- **Major arbitration — the backend.** The V2 plan committed to *"mock remote = final backend"* (a client-side Ktor `MockEngine`). That commitment was **superseded**: starting with `v0.5.0` (2026-07-22) the project built a real **simulated Open Banking server** — Ktor Server + Exposed/PostgreSQL + JWT + BCrypt, covered by end-to-end Testcontainers tests — matching the architecture already scoped in `CONTEXT.md` §3. It's a bigger build than planned, but it's what makes the CI, deployment manual, and Bloc 4 supervision/security deliverables demonstrable with real artifacts instead of a description of what a mock would need.
- **New scope not in the original V2 backlog.** A full **Bloc 4 (Maintenir en condition opérationnelle)** deliverable set was added mid-project: dependency-update process (`docs/dependances.md` + Dependabot), supervision & alerting (`docs/supervision.md` + `/health`, `/health/ready`), an anomaly-logging process (`docs/anomalies/processus.md`, GitHub issue template, `ANO-001`), and improvement recommendations (`docs/recommandations-amelioration.md`). None of this existed as a story in the V2 backlog. A unified **Wisepenny UI component library** + UX-cohesion spec, and a GDPR-facing **profile/privacy screen** (data reset, privacy notice, About) were also built without a corresponding US in the original plan.
- **Stories from the original backlog still open.** US-08 (badge system) was never built — no `Badge` model/table in the codebase. US-12 (daily reminder) has a UI toggle only; no platform notification scheduling (`AlarmManager` / `UNUserNotificationCenter`) exists. US-13 (OPQUAST/RGAA accessibility audit) and a formal OWASP audit document (part of US-14) are explicitly flagged as **not yet covered** in `docs/recommandations-amelioration.md`. US-17 (a written acceptance-test plan with a Gherkin execution log) was never produced as a document — automated tests exist (3 client test files, 1 server integration test file, CI-enforced) but there is no `docs/acceptance-tests/` folder. TECH-03 (cumulative ADRs, 8+ files) was never delivered as literal ADR files — no `docs/decisions/` folder exists; decisions are instead narrated in `CHANGELOG.md` and `CONTEXT.md`. iOS was never added to CI (`ci.yml` runs Android/JVM unit tests + server integration tests only) — so the Definition of Done clause "Android **and** iOS builds green (CI-verified)" has not actually been enforced by CI for any story.
- **Where this leaves the plan.** The backlog table, Gherkin scenarios, DoR/DoD, ceremonies and per-sprint breakdowns below are kept as originally written — still useful as the *committed* plan for arbitration purposes — but each section now carries a short "Actual" note pointing at what really happened. See the **Actual Release Timeline**, the updated **Product Backlog** status column, **Delivered Beyond the Original Backlog**, the updated **RNCP Criteria Mapping**, and **Current Status & Remaining Work** near the end for the full picture.

### Actual Release Timeline

| Version | Date | Day # | Roughly maps to (V2 plan) | Content |
|---|---|---|---|---|
| 0.1.0 | 2026-06-02 | Day 7 | S1 (TECH-00, US-01, US-02) + S2 (US-03–05) + part of S3 (US-06, US-07) | KMP foundations, Challenge domain/data/UI, Goals, Dashboard, Compose navigation, Koin DI |
| 0.2.0 | 2026-06-25 | Day 30 | S4 (US-11) | Narrative learning module ("Apprendre"), no quizzes |
| 0.3.0 | 2026-07-11 | Day 46 | S4 (US-10) | Onboarding flow v1, profile ViewModel, back-gesture policy |
| 0.4.0 | 2026-07-14 | Day 49 | S1 (TECH-01) / S3 (US-15, Android-only) | CI pipeline (Android unit tests), auto-save & mapper tests, cross-platform `BackHandler` |
| 0.5.0 | 2026-07-22 | Day 57 | **Not in the V2 backlog as scoped** — supersedes "mock remote = final backend" | Real Ktor server, PostgreSQL/Exposed, JWT auth, `API_SPEC.md`, Testcontainers e2e tests, server CI, deployment/user manuals (US-16 partial) |
| 0.6.0 | 2026-07-24 | Day 59 | Not in the V2 backlog | Wisepenny design-system components, UX-cohesion spec, Detekt + Ktlint, daily-challenge promo card |
| 0.7.0 | 2026-07-27 | Day 62 | Loosely covers the RGPD half of US-14 | Profile screen, GDPR privacy notice, full data reset (droit à l'effacement), About window |
| 0.8.0 | 2026-08-16 | Day 82 | New Bloc 4 scope, not in the V2 backlog | `/health/ready` probe, `docs/supervision.md`, structured HTTP logging + rotation, Dependabot, anomaly process + `ANO-001` fix |
| *unreleased* | 2026-08-19 → 2026-09-14 | Day 85–111 | BC04 doc completion + US-10 rework | `docs/dependances.md`, `docs/recommandations-amelioration.md`, `docs/support-client-exemple.md`; profile/settings polish + custom tab bar; onboarding rebuilt as a 7-step wizard (categories, 6-month horizon, demo bank) |

*(The V2 plan's "1 SP ≈ 1h15" velocity tracking was not kept up after Day 1 — there are no daily hour logs, so story points cannot be reconstructed retroactively. Elapsed calendar time and shipped versions are the honest signal available.)*

---

## V1 Audit — Corrections Applied

*(Historical — the V1→V2 audit that produced the plan below. Unchanged.)*

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

| ID | User Story | MVP Feature | SP | Priority | Status (2026-09-15) | Note |
|---|---|---|---|---|---|---|
| US-01 | Track an active savings challenge (read-only) | Active Challenge | 5 | Must | ✅ Done | Shipped v0.1.0 |
| US-02 | Validate each challenge day (write + streak) | Active Challenge | 5 | Must | ✅ Done | Shipped v0.1.0 |
| US-03 | View my savings goals | Savings Goal | 5 | Must | ✅ Done | Shipped v0.1.0 |
| US-04 | Manually add money toward a goal | Savings Goal | 3 | Must | ✅ Done | Shipped v0.1.0 |
| US-05 | Auto-credit from challenge to goal | Savings Goal | 3 | Must | ✅ Done | Shipped v0.1.0 |
| US-06 | Aggregated dashboard as home screen | Dashboard | 8 | Must | ✅ Done | Shipped v0.1.0 |
| US-07 | 4-tab bottom navigation | Dashboard | 3 | Must | ✅ Done | Shipped v0.1.0; bottom bar rebuilt custom (Sept 2026) |
| US-08 | Earn badges (6 initial badges) | Gamification | 5 | Must | ❌ Not built | No `Badge` model/table in the codebase |
| US-09 | Global app streak | Habit tracking | 3 | Must | ⚠️ Partial | Streak logic exists on Dashboard/Challenge; not confirmed as a distinct cross-feature "global" streak |
| US-10 | 4-screen express onboarding | Onboarding | 8 | Must | ✅ Done (reworked) | v1 in v0.3.0; rebuilt as a 7-step wizard 2026-09-14 |
| US-11 | Narrative learning module | Learning | 5 | Must | ✅ Done | Shipped v0.2.0 |
| US-12 | Daily challenge reminder (local notification) | Reminders | 3 | Must | ⚠️ Partial | UI toggle only (Profile screen); no `AlarmManager`/`UNUserNotificationCenter` scheduling found |
| US-13 | OPQUAST accessibility audit + fixes | Accessibility | 5 | Must (RNCP) | ⏳ Open | Flagged as not yet covered in `docs/recommandations-amelioration.md` (axis 2) |
| US-14 | OWASP security audit + local encryption | Security | 5 | Must (RNCP) | ⚠️ Partial | JWT + BCrypt + input validation shipped; no formal `docs/security/owasp-mobile-audit.md` written |
| US-15 | Complete CI/CD (Android + iOS + tests + lint) | DevOps | 3 | Must (RNCP) | ⚠️ Partial | CI runs Android/JVM unit tests + server integration tests; no iOS runner; Detekt/Ktlint not wired into CI |
| US-16 | 3 manuals (deployment, user, update) | Documentation | 5 | Must (RNCP) | ✅ Done | `docs/deploiement.md`, `docs/utilisation.md`, `docs/mise-a-jour.md` |
| **US-17** | **Test plan + bug correction plan** | **Acceptance Testing** | **3** | **Must (RNCP)** | ⏳ Open | Automated tests + `docs/anomalies/` process exist; no `docs/acceptance-tests/test-plan.md` with a Gherkin execution log |
| TECH-00 | Residual KMP setup (theme, DB, Ktor, Koin, nav) | Foundation | 8 | Must | ✅ Done | Shipped v0.1.0 |
| TECH-01 | Minimal CI pipeline Sprint 1 | DevOps | 2 | Should | ✅ Done | Shipped v0.4.0 |
| TECH-02 | Demo mode + script + backup video | Defense Prep | 3 | Must | ⏳ Open | No demo-mode/backup-video artifact found |
| TECH-03 | Cumulative ADRs (8+ files) | Documentation | _0_ | Continuous | ❌ Not built | No `docs/decisions/` folder; decisions narrated in `CHANGELOG.md`/`CONTEXT.md` instead |
| **Backlog Total** | | | **85 SP** | | | |
| **Sprint-Committed Total** | | | **87 SP** | | | |

**2 SP delta explained** *(as planned)*. Difference comes from TECH-02 (demo mode) split 2+3 across S1 (debug button) and S4 (full demo mode), plus TECH-04/05/06/07 in Sprint 2 which are operational task breakdowns of stories US-03/US-04/US-05 (already counted in backlog). Expected delta from story→task conversion in Scrum.

**Velocity note** *(as planned — not how it was actually tracked; see Execution Summary)*. 1 SP ≈ 1h15 effective on average for KMP stack with tests + ADR + dual build. Capacity 4 sprints × 26.5h = 106h → **~87 SP achievable**. Backlog fits **without margin**: Sunday buffers (12h) absorb variance.

### Delivered Beyond the Original Backlog

Work that shipped without a corresponding story in the V2 plan above:

| Item | Release | RNCP value |
|---|---|---|
| Real simulated Open Banking server (Ktor + Exposed + PostgreSQL + JWT + BCrypt) | v0.5.0 | Supersedes "mock remote"; gives C2.1/C2.2/C4 real artifacts instead of a description |
| API end-to-end tests with Testcontainers | v0.5.0 | C2.2.2 |
| Wisepenny UI component library + UX-cohesion spec | v0.6.0 | Centralizes styling — `docs/recommandations-amelioration.md` notes this gives leverage for the still-open accessibility pass |
| Detekt + Ktlint static analysis | v0.6.0 | C2.2.3 (maintainability); not yet wired into CI |
| GDPR profile & privacy screen (data reset, privacy notice, About) | v0.7.0 | RGPD compliance |
| Readiness/liveness probes + structured HTTP logging | v0.8.0 | C4.1.2 |
| Dependabot + dependency-update process doc | v0.8.0 | C4.1.1 |
| Anomaly logging process + issue template + `ANO-001` | v0.8.0 | C4.2.1 |
| Improvement-recommendations doc | unreleased | C4.3.1 |
| Client-support example doc | unreleased | Bloc 4 |
| Onboarding rebuilt as a 7-step wizard (categories, fixed 6-month horizon, demo bank picker) | unreleased | Reworks US-10 beyond the original 4-screen scope |

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

> **Actual (2026-09-15).** The "Android **and** iOS builds green (CI-verified)" clause was never enforced by CI in practice — `ci.yml` only runs Android/JVM unit tests and server integration tests, no iOS runner. Same for the ADR and `docs/acceptance-tests/` clauses: no story went through those two boxes as literally written. Treat this DoD as the target, not a record of what was checked per story.

---

## Lightweight Ceremonies (60 min max / sprint)

| Ceremony | When | Duration | Format |
|---|---|---|---|
| Sprint Planning | Monday 9am | **20 min** | Backlog validation + Day 1 tasks |
| Daily Check-in | Every day 9am | **5 min** | 3 lines in `daily-log.md` |
| Sprint Review | Friday 5pm | **20 min** | 1-2 min demo video + dashboard update |
| Sprint Retro | Friday 5:20pm | **20 min** | 3 columns start/stop/continue in `retro-sX.md` |

**Refinement removed** — replaced by 10 min at end of Sprint Planning to validate DoR of current sprint stories only. Future sprint stories already have pre-validated DoR in this document.

> **Actual (2026-09-15).** No `daily-log.md` or `retro-sX.md` files exist in the repo — this ceremony cadence was not kept as written. `CHANGELOG.md` entries and commit history are the de facto substitute record.

---

# SPRINT 1 — Foundation & Active Challenge

**Day 1 (Monday) → Day 7 (Sunday).** Committed capacity 26.5h (Sunday buffer). Commitment: **22 SP**.

> **Actual.** TECH-00, US-01, US-02 shipped — and, ahead of schedule, the whole S2/S3 CRUD surface (Goals, Dashboard, navigation) shipped together in `v0.1.0` on 2026-06-02, about a week after Day 1. TECH-01 (CI) followed much later, in `v0.4.0` (2026-07-14, Day 49). TECH-02's debug button is not confirmed in the current codebase.

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

> **Actual.** US-03/04/05 shipped as part of `v0.1.0` alongside S1, not as a standalone week-2 sprint. TECH-04–07 (projection, goal picker, S1 refactor, ADR-002) are not individually verifiable in the current codebase or docs — no `docs/decisions/ADR-002*` file exists.

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

> **Actual.** US-06/07 shipped as part of `v0.1.0` alongside S1/S2. US-08 (badges) was never built. US-09 (streak) exists on Dashboard/Challenge but is not confirmed as a distinct global mechanism. US-15 (CI/CD) shipped partially in `v0.4.0` (2026-07-14) — Android/server tests only, no iOS runner, no Detekt/Ktlint step in CI.

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

> **Actual.** US-10 shipped in `v0.3.0` (2026-07-11) — much earlier than the original S4 slot — then rebuilt as a 7-step wizard on 2026-09-14. US-11 also shipped earlier than planned, in `v0.2.0` (2026-06-25). US-12 is UI-only (no scheduling). US-13 (accessibility) and the formal US-14 OWASP audit document are still open — see `docs/recommandations-amelioration.md`. US-16 (3 manuals) is done. US-17 (test plan doc) was never written. TECH-02 (demo mode/backup video) was not found in the repo.

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

> **Actual outcome of this cut plan (2026-09-15).** Cut #3 (iOS CI) happened by default rather than by decision — iOS was never wired into `ci.yml`. Cuts #1 and #2 aren't applicable yet since accessibility work and the learning module's section count were never revisited against this plan. The formally "never cut" items — OWASP audit doc, OPQUAST audit, test plan, demo mode, backup video — are the ones still outstanding; see **Current Status & Remaining Work** below.

### S4 Risks

| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| KMP reminders (expect/actual notif) more complex than 2h | High | Medium | Strict time-box, otherwise Android only + ADR |
| OWASP audit reveals major vulnerability | Low | High | Audit D23, 2 days to fix |
| Blocking bug in test plan D25 | Medium | High | 2h dedicated fix D25, otherwise plan B = document as known bug |
| Demo crashes in defense | Medium | High | Backup video mandatory D27 |

---

## Current Status & Remaining Work (as of 2026-09-15)

Per `docs/recommandations-amelioration.md` (BC04, self-assessed) and the gaps identified throughout this document:

1. **Accessibility (US-13, RNCP-critical).** No RGAA/OPQUAST audit performed yet. The now-centralized Wisepenny component library (v0.6.0) means a single pass over shared components should propagate everywhere — flagged as high-leverage, 5–8 person-days.
2. **Formal security audit doc (US-14, RNCP-critical).** JWT, BCrypt and input validation are implemented, but there is no written OWASP Top 10 mapping document for the jury dossier.
3. **Acceptance test plan (US-17, RNCP-critical).** Automated tests exist and run in CI; a written Gherkin-based `docs/acceptance-tests/test-plan.md` with execution status per scenario has not been produced.
4. **iOS in CI.** `ci.yml` never gained an iOS/macOS runner — the DoD clause "Android and iOS builds green (CI-verified)" has not been enforced by CI for any story. Flagged as axis 4 in `docs/recommandations-amelioration.md`.
5. **Demo mode & backup video (TECH-02).** Not found in the codebase or docs — still needed before defense.
6. **ADRs as files (TECH-03).** Decisions exist in `CHANGELOG.md`/`CONTEXT.md` narrative form, not as the originally committed `docs/decisions/ADR-*.md` files.
7. **Badges (US-08) and scheduled reminders (US-12).** Deferred; not built beyond a UI toggle for reminders.
8. **Longer-term, non-blocking** (per `docs/recommandations-amelioration.md`): real bank connection (axis 1), containerized deployment with a live uptime monitor (axis 3), offline sync/conflict resolution (axis 5) — all explicitly scoped as post-MVP.

---

## Project Dashboard (to be maintained)

> **Note (2026-09-15).** This weekly SP/hours table was never filled in — the project did not run on a weekly-sprint cadence in practice. See **Actual Release Timeline** near the top of this document for the real record.

| Sprint | Committed SP | Completed SP | Velocity | Actual hours / budget | Sprint goal ✓ |
|---|---|---|---|---|---|
| S1 | 22 | _____ | _____ | _____ / 26.5h | _____ |
| S2 | 21 | _____ | _____ | _____ / 26.5h | _____ |
| S3 | 22 | _____ | _____ | _____ / 26.5h | _____ |
| S4 | 22 | _____ | _____ | _____ / 26.5h | _____ |
| **Total** | **87** | _____ | _____ | _____ / 106h | _____ |

**Velocity note** *(as planned)*. 87 SP × 1.22h/SP ≈ 106h. Fits exactly within committable capacity. Sundays D7, D14, D21 are pure buffer (12h additional available if slippage). D28 is committed for final rehearsals.

Direct RNCP deliverable for **C3.2.1** (progress monitoring) and **C3.2.2** (arbitrations).

---

## RNCP Criteria → Actual Mapping (updated 2026-09-15)

| Criterion | Status | Artifact |
|---|---|---|
| C2.1.1, C2.1.2 (environments, CI) | ⚠️ Partial | `.github/workflows/ci.yml` — Android/JVM unit tests + server Testcontainers integration tests; no iOS runner |
| C2.2.1 (prototype) | ✅ | Working Android/iOS/desktop KMP app since `v0.1.0` |
| C2.2.2 (unit tests) | ⚠️ Partial | 3 client test files (`StartupScreenTest`, `MapperTest`, `GoalRepositoryAutoSaveTest`) + 1 server integration test file (`ApiTest`); no formal coverage measurement |
| C2.2.3 (security, accessibility, maintainability) | ⚠️ Partial | Security: JWT + BCrypt + input validation shipped, no formal OWASP audit doc. Accessibility: not yet covered — open axis in `docs/recommandations-amelioration.md`. Maintainability: Detekt + Ktlint configured but not enforced in CI |
| C2.2.4 (progressive deployment) | ⚠️ Partial | CI pipeline exists; no containerized/staged deployment (open axis 3 in `docs/recommandations-amelioration.md`) |
| C2.3.1, C2.3.2 (acceptance tests, bugs) | ⚠️ Partial | Automated tests + `docs/anomalies/` process (`ANO-001`) cover bug tracking; no `docs/acceptance-tests/test-plan.md` with a Gherkin execution log yet |
| C2.4.1 (documentation) | ✅ | `docs/deploiement.md`, `docs/utilisation.md`, `docs/mise-a-jour.md` |
| C3.1 (planning) | ✅ | This document (original plan + execution record) |
| C3.2.1 (monitoring) | ⚠️ Partial | `CHANGELOG.md` + Git history are the de facto monitoring record; `daily-log.md`/`retro-sX.md` as originally planned were not kept |
| C3.2.2 (arbitrations) | ✅ | This document's Execution Summary — mock→real backend pivot, Bloc 4 scope insertion, timeline extension |
| C3.4.1, C3.4.2 (client follow-up, demo) | ⏳ Pending | Demo script + backup video (TECH-02) not yet produced |
| C4.1.1 (dependency updates) | ✅ | `docs/dependances.md`, `.github/dependabot.yml` |
| C4.1.2 (supervision) | ✅ | `docs/supervision.md`, `/health` and `/health/ready` routes |
| C4.2.1 (anomaly logging) | ✅ | `docs/anomalies/processus.md`, `ANO-001` |
| C4.3.1 (improvement recommendations) | ✅ | `docs/recommandations-amelioration.md` |
| Bloc 4 (client support) | ✅ | `docs/support-client-exemple.md` |

**C3.3 (team).** Solo project, to be adapted in defense: justified solo context + OPQUAST accessibility as disability proxy + communication mastery via ADRs and written retros.

---

## Pre-Day 1 Commitments (to be formally locked)

- [x] Streak policy: strict (ADR-001)
- [x] ~~Mock remote = final backend~~ ⚠️ **Superseded 2026-07-22 (`v0.5.0`)** — see Execution Summary: a real simulated Open Banking server was built instead
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

| Check | Status (as planned) | Status (2026-09-15) |
|---|---|---|
| Coherent SP arithmetic (22+21+22+22 = 87) | ✅ | N/A — SP tracking was not kept after Day 1 |
| Coherent hour capacity (4×26.5 = 106h) | ✅ | N/A — no daily hour logs exist |
| Realistic velocity (87 SP ≈ 106h at 1.22h/SP) | ✅ | ❌ Actual elapsed time is ~4× the planned 28 days, and still open |
| No overloaded sprint (max commitment = 22 SP) | ✅ | N/A |
| C2.3.1 (test plan) explicitly covered | ✅ | ⏳ Pending — automated tests + anomaly process exist, formal Gherkin test-plan doc not written |
| Buffer exists (Sundays except D28) | ✅ | N/A — weekly buffer model was not used in practice |
| Documented cut plan for S4 | ✅ | ⚠️ Not exercised as written; real gaps (accessibility, iOS CI) are documented instead in `docs/recommandations-amelioration.md` |
| Risks identified per sprint | ✅ | ✅ Kept as historical planning artifact |
| Pre-Day 1 commitments locked | ✅ | ⚠️ One commitment (mock remote = final backend) was revisited and superseded |

**Plan validated 2026-05-25. Ready to start Day 1 Monday morning.** *(Execution record added 2026-09-15 — project ongoing; see Current Status & Remaining Work above for what's left.)*
