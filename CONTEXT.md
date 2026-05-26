# Wisepenny — Project Context

> Reference document for AI coding assistants (Claude Code, Cursor, etc.) and onboarding.
> Keep this file at the repo root and update it whenever stack, scope, or constraints change.

---

## 1. Project overview

**Wisepenny** is a mobile application for **financial education and micro-savings**, targeting users **aged 18–26** in France. The product is commissioned by a retail bank as a customer-acquisition and financial-literacy tool, and it serves as the capstone project for the **RNCP39583 — Expert en développement logiciel** certification (Ynov Campus).

### Problem statement (official, V2)

> Comment concevoir une application mobile d'éducation financière destinée aux 18–26 ans, combinant pédagogie gamifiée et visualisation personnalisée des dépenses, afin de favoriser l'adoption de meilleures habitudes budgétaires tout en respectant les exigences de sécurité, de conformité et de faisabilité propres au secteur bancaire ?

### Value proposition

Combine **narrative learning modules (Brilliant.org-style)**, **daily/weekly micro-savings challenges**, and **personalized spending visualization** to help young adults build durable budgeting habits and reach concrete goals (travel, major purchase, personal/professional project).

---

## 2. MVP scope

### In scope

- Onboarding flow with savings-goal profile creation
- **Narrative learning modules** — short, story-driven lessons in the Brilliant.org tradition (scrollable concept pages with inline examples, not multiple-choice tests)
- Module progression with XP and unlock logic
- Daily and weekly micro-savings challenges
- Habit tracking and streaks
- Spending visualization dashboard
- Gamification: XP, badges, levels
- Reminders and notifications
- **Simulated Open Banking API** (no real bank integration)

### Pedagogical approach — no quizzes

Wisepenny does **not** use interactive quizzes. Learning happens through narrative modules that walk the user through a financial concept with worked examples, visual analogies, and contextual framing (compound interest, Livret A vs. compte courant, budget envelopes, etc.). Progression is driven by completing module pages and applying concepts in real savings challenges — not by passing knowledge tests.

**Why this choice:**
- Closer to how the target audience (18–26) actually consumes content on mobile
- Removes the "exam anxiety" friction that discourages drop-offs after the first failed attempt
- Lets us redirect engineering effort from quiz state management toward richer module content and challenge mechanics
- Cleaner MVP scope, easier to justify in jury defense (one well-built learning surface beats two half-built ones)

### Out of scope (MVP)

- Quizzes, knowledge tests, multiple-choice assessments (deliberate — see above)
- Real Open Banking / PSD2 integration with production banks
- Social features (friend leaderboards, sharing) — backlog
- In-app payments or money movement
- Web companion app
- Multi-language (FR only for MVP)

### Why the simulated banking API

The simulated API is a **deliberate architectural decision**, not a limitation. It preserves project feasibility within the RNCP timeframe, isolates the team from PSD2/DSP2 regulatory burden, and lets us demonstrate the full UX and security posture without onboarding a real bank partner. This must be framed positively in jury defense.

---

## 3. Technical stack

### Mobile (client)

- **Compose Multiplatform** — UI layer, shared between Android and iOS
- **Kotlin Multiplatform (KMP)** — business logic, networking, persistence
- **SQLDelight + SQLite** — local cache and offline-first storage
- **kotlinx.serialization** — JSON (de)serialization
- **Ktor Client** — HTTP client for the backend API

### Backend (server)

- **Kotlin + Ktor Server** — REST API
- **Exposed (ORM) + PostgreSQL** — relational, ACID-compliant store (required for financial data integrity)
- **kotlinx.serialization** — shared serialization with the client

### Tooling & design

- **Stitch (Google AI)** — UI generation from prompts
- **Figma** — design system handoff
- **Gradle (Kotlin DSL)** — build
- **Git + GitHub** — version control
- **GitHub Actions** — CI/CD (planned)

### Standards & compliance

- **RGPD** — data minimization, explicit consent, right to erasure
- **OWASP Top 10** — security baseline
- **RGAA / OPQUAST** — accessibility (must be justified explicitly in deliverables)
- **Eco-design** — lightweight architecture, limited data transfers, no feature bloat
- **Open Banking (simulated)** — API contract mirrors real PSD2 patterns

### Rejected alternatives (documented for RNCP C1.3.2)

| Rejected | Reason |
|---|---|
| Firebase | Insufficient control over data residency, NoSQL unsuitable for financial records, Google lock-in |
| Spring Boot | Java-first, heavyweight, slower iteration than Ktor for our team |
| MongoDB | Non-ACID, unsuitable for financial transactions |
| Room | Android-only, breaks the KMP shared-logic story |
| React Native / Flutter | Less direct path to a Kotlin-first backend; CMP gives us a single language top-to-bottom |

---

## 4. Visual design system

Finalized **dark-mode premium fintech** identity. Reference aesthetic: Revolut Ultra / Ramp / Halo Lab "Sinport" (Dribbble).

### Color tokens

```
Background       #0A0F1A  (near-black, slight blue tint)
Surface          #141B2A  (elevated cards on dark)
Divider          #1F2937

Accent primary   #5EEAD4  (mint — CTAs, key numbers, active states)
Accent darker    #2DD4BF
Accent light     #CCFBF1

Text on dark     #FFFFFF / #E5E7EB / #9CA3AF / #6B7280
Card light       #FFFFFF / #F3F4F6
Text on light    #0A0F1A / #4B5563

Success          #22C55E
Danger           #EF4444
Warning          #F59E0B
Info             #3B82F6
```

### Typography

- **Family:** Inter (system fallback)
- **Display XL** 48–64px / weight 800 / letter-spacing -0.02em — hero numbers
- **Display L** 32–40px / weight 800 — amounts, key figures
- **H1** 24–28px / weight 700
- **H2** 20px / weight 600
- **Body** 15–16px / weight 400 / line-height 1.5
- **Caption** 12–13px / weight 500

### Shape & spacing

- **Radius scale:** 12 / 16 / 20 / 24 / 28 px
- **Spacing scale:** 4 / 8 / 12 / 16 / 20 / 24 / 32 / 48 / 64 px

### Compose Multiplatform theme files (next deliverable)

```
shared/src/commonMain/kotlin/com/wisepenny/theme/
├── Color.kt          # hex tokens above
├── Typography.kt     # Inter scale
├── Shape.kt          # 12/16/20/24/28
├── Spacing.kt        # 4..64 scale
└── WisepennyTheme.kt # MaterialTheme wrapper
```

### Screens scoped for Stitch generation

1. Dashboard / Home
2. Learning Module Detail (narrative, Brilliant.org-style)
3. Active Challenge
4. Savings Goal Profile

Full screen prompts live in `/docs/design/02_screen_prompts.md`. The system prompt that sets the Stitch visual identity lives in `/docs/design/01_system_prompt_style_guide.md`.

---

## 5. RNCP39583 — certification structure

The project is the evaluation vehicle for the **Expert en développement logiciel** certification. Four blocs, each with its own jury format.

### Bloc 1 — Cadrer un projet de développement (oral defense)

Cover: stakeholder mapping, client brief analysis, SWOT, feasibility, risks, technology watch, comparative technical study, architecture, estimation, client pitch.

**Current status:**
- ✅ 1.1.1 stakeholder mapping + RACI
- ✅ 1.1.2 client brief analysis
- ✅ 1.2.1 SWOT
- ✅ 1.3.2 comparative technical study
- 🔲 introduction, persona
- 🔲 C1.2.2 feasibility
- 🔲 C1.2.3 risks
- 🔲 C1.3.1 technology watch
- 🔲 C1.5 architecture
- 🔲 C1.4.1 / C1.4.2 estimation (person-days, budget)
- 🔲 C1.6 jury argumentation

### Bloc 2 — Concevoir et développer (written dossier + source code)

Deliverables: source code, deployment manual, user guide, update guide, OWASP/accessibility justification, CI/CD protocol, test harness, recette + bug-fix plan.

### Bloc 3 — Coordonner et piloter (oral + live demo)

Deliverables: methodology choice (Agile/Scrum/Kanban), planning (Gantt/PERT), RACI, arbitrage example, managerial style, skills development plan, client status reports, live software demo.

### Bloc 4 — Maintenir en condition opérationnelle (written dossier)

Deliverables: dependency update process, supervision/alerting system, anomaly logging, fix deployment, improvement recommendations, changelog, support collaboration example.

### Mandatory cross-cutting requirements

- **C1.3.2 five axes:** security, system environments, networks, accessibility, environmental impact — all five must be covered
- **C3.1 and C3.3.1:** tasks must account for people with disabilities
- **C1.4.1:** features hierarchized (primary/secondary/complementary), workload in person-days

---

## 6. Working principles

- **Don't assume — ask or search.** If anything is unclear, ambiguous, or not explicitly stated (project context, intent, library behavior, API contract, RNCP criterion interpretation, design decision), **stop and ask** before acting. If the answer is factual and verifiable (library version, framework API, regulation wording, dependency compatibility, etc.), **search the web** to confirm rather than guessing from training data. Silent assumptions are the most expensive failure mode for this project.
- **Dual-purpose decisions:** every technical choice must satisfy both engineering needs and serve as a jury-justifiable proof point. Frame as *RNCP criterion → evidence → project explanation*.
- **Rejected options are documented:** see §3 above. The argument matters as much as the choice.
- **Practical tradeoffs over theoretical optimums.** Jury-justifiable beats academically perfect.
- **MVP discipline.** New features pass through scope/feasibility/jury-value gate before entering the backlog.
- **French for all RNCP deliverables.** Code, identifiers, commit messages in English. UI strings and documentation for the jury in French.
- **App name everywhere:** `Wisepenny` (Kotlin packages: `com.wisepenny.*`).

---

## 7. Key references

- **Design inspiration:** Revolut Ultra, Ramp, Halo Lab / Sinport (Dribbble)
- **UX inspiration:** Brilliant.org (narrative learning modules — the primary pedagogical reference)
- **Standards:** RGPD, OWASP Top 10, RGAA, OPQUAST, PSD2 (simulated)
- **Certification:** RNCP39583 — Expert en développement logiciel (Ynov Campus)
