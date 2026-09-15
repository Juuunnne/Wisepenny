# Wisepenny — Plan de sprint & bilan d'exécution

> **Statut.** Document en deux parties. La **Partie 1** est le plan de sprint V2 original sur 4 semaines, validé et engagé avant le Jour 1 (26/05/2026) — conservé tel quel ci-dessous pour la traçabilité (RNCP C3.1, C3.2.2). La **Partie 2**, ajoutée le 15/09/2026, est le **bilan d'exécution** : ce qui a réellement été livré, les arbitrages de périmètre/planning effectués en cours de route, et ce qui reste ouvert. Lire d'abord le Résumé d'exécution — il indique quelles parties de la Partie 1 relèvent de l'intention historique plutôt que de la réalité actuelle.
>
> **Cadre méthodologique (tel que planifié).** Scrum solo adapté. 4 sprints d'une semaine. Capacité 4,5h × 5 jours ouvrés + 4h le samedi = **26,5h « engageables » / sprint**, le dimanche étant conservé comme **tampon de rattrapage non engagé**. Total engageable : **106h sur 28 jours**. *(Ce n'est pas ce qui s'est réellement passé — voir le Résumé d'exécution.)*
>
> **Stack (telle que planifiée).** Compose Multiplatform, Kotlin Multiplatform, SQLDelight, Ktor, Koin, kotlinx.serialization, kotlinx.datetime.
>
> **Stack (telle que réellement construite).** Tout ce qui précède, plus un vrai backend : **Ktor Server, Exposed ORM, PostgreSQL, authentification JWT, BCrypt**, testés de bout en bout avec **Testcontainers** ; analyse statique **Detekt + Ktlint** ; veille des dépendances **Dependabot**.
>
> **Mise en place déjà faite (Jour 1).** Projet KMP créé, arborescence prête, charte graphique validée.

---

## Résumé d'exécution (ajouté le 15/09/2026)

Le plan ci-dessous a été validé et figé avant le Jour 1. Voici ce qui s'est réellement passé au cours des ~16 semaines suivantes.

- **Planning.** Le projet s'étend du **26/05/2026 au 15/09/2026 (112 jours, ~16 semaines)** et est toujours en cours — soit environ **4× la fenêtre de 28 jours** engagée dans le plan V2. La cadence formelle de journal quotidien / rétro prévue dans la section Cérémonies n'a pas été tenue ; `CHANGELOG.md` et l'historique Git en tiennent lieu de registre de fait.
- **Arbitrage majeur — le backend.** Le plan V2 engageait un « remote mocké = backend final » (un `MockEngine` Ktor côté client). Cet engagement a été **dépassé** : à partir de la `v0.5.0` (22/07/2026), le projet a construit un vrai **serveur d'Open Banking simulé** — Ktor Server + Exposed/PostgreSQL + JWT + BCrypt, couvert par des tests de bout en bout Testcontainers — conforme à l'architecture déjà cadrée dans `CONTEXT.md` §3. C'est une réalisation plus lourde que prévu, mais c'est elle qui rend démontrables, avec de vrais artefacts (et non une description de ce qu'exigerait un mock), la CI, le manuel de déploiement et les livrables de supervision/sécurité du Bloc 4.
- **Périmètre nouveau, absent du backlog V2 initial.** Un jeu complet de livrables **Bloc 4 (Maintenir en condition opérationnelle)** a été ajouté en cours de projet : processus de mise à jour des dépendances (`docs/dependances.md` + Dependabot), supervision et alerte (`docs/supervision.md` + `/health`, `/health/ready`), un processus de consignation des anomalies (`docs/anomalies/processus.md`, formulaire d'issue GitHub, `ANO-001`), et des recommandations d'amélioration (`docs/recommandations-amelioration.md`). Rien de tout cela n'existait comme user story dans le backlog V2. Une bibliothèque de composants **Wisepenny** unifiée + une spec de cohésion UX, ainsi qu'un **écran profil/confidentialité** conforme RGPD (réinitialisation des données, notice de confidentialité, À propos), ont également été construits sans US correspondante dans le plan initial.
- **User stories du backlog initial toujours ouvertes.** US-08 (système de badges) n'a jamais été construite — aucun modèle/table `Badge` dans le code. US-12 (rappel quotidien) n'a qu'un interrupteur d'interface ; aucune planification de notification côté plateforme (`AlarmManager` / `UNUserNotificationCenter`) n'existe. US-13 (audit d'accessibilité OPQUAST/RGAA) et un document d'audit OWASP formel (partie de US-14) sont explicitement signalés comme **non encore couverts** dans `docs/recommandations-amelioration.md`. US-17 (un plan de tests d'acceptation écrit avec un journal d'exécution Gherkin) n'a jamais été produite comme document — des tests automatisés existent (3 fichiers de tests côté client, 1 fichier de tests d'intégration côté serveur, exécutés en CI) mais il n'y a pas de dossier `docs/acceptance-tests/`. TECH-03 (ADR cumulatifs, 8 fichiers et plus) n'a jamais été livrée sous forme de fichiers ADR littéraux — aucun dossier `docs/decisions/` n'existe ; les décisions sont racontées dans `CHANGELOG.md` et `CONTEXT.md` à la place. iOS n'a jamais été ajouté à la CI (`ci.yml` exécute uniquement les tests unitaires Android/JVM + les tests d'intégration serveur) — la clause de Definition of Done « builds Android **et** iOS au vert (vérifié par CI) » n'a donc en réalité jamais été appliquée par la CI, pour aucune story.
- **Où cela laisse le plan.** Le tableau de backlog, les scénarios Gherkin, la DoR/DoD, les cérémonies et les découpages par sprint ci-dessous sont conservés tels qu'écrits à l'origine — toujours utiles comme plan *engagé* à des fins d'arbitrage — mais chaque section porte désormais une courte note « Réalisé » indiquant ce qui s'est vraiment passé. Voir la **Chronologie réelle des versions**, la colonne de statut ajoutée au **Backlog produit**, **Livré au-delà du backlog initial**, le **Mapping RNCP** mis à jour, et **État actuel & travail restant** en fin de document pour la vue d'ensemble.

### Chronologie réelle des versions

| Version | Date | Jour n° | Correspond approximativement à (plan V2) | Contenu |
|---|---|---|---|---|
| 0.1.0 | 02/06/2026 | Jour 7 | S1 (TECH-00, US-01, US-02) + S2 (US-03–05) + une partie de S3 (US-06, US-07) | Fondations KMP, domaine/données/UI Challenge, Objectifs, Tableau de bord, navigation Compose, DI Koin |
| 0.2.0 | 25/06/2026 | Jour 30 | S4 (US-11) | Module d'apprentissage narratif (« Apprendre »), sans quiz |
| 0.3.0 | 11/07/2026 | Jour 46 | S4 (US-10) | Parcours d'onboarding v1, ViewModel de profil, politique de geste retour |
| 0.4.0 | 14/07/2026 | Jour 49 | S1 (TECH-01) / S3 (US-15, Android uniquement) | Chaîne CI (tests unitaires Android), tests d'auto-épargne et de mappers, `BackHandler` multiplateforme |
| 0.5.0 | 22/07/2026 | Jour 57 | **Absent du backlog V2 tel que cadré** — dépasse l'engagement « remote mocké = backend final » | Vrai serveur Ktor, PostgreSQL/Exposed, authentification JWT, `API_SPEC.md`, tests e2e Testcontainers, CI serveur, manuels de déploiement/utilisation (US-16 partielle) |
| 0.6.0 | 24/07/2026 | Jour 59 | Absent du backlog V2 | Composants du design system Wisepenny, spec de cohésion UX, Detekt + Ktlint, carte de promotion du défi quotidien |
| 0.7.0 | 27/07/2026 | Jour 62 | Couvre partiellement le volet RGPD de US-14 | Écran profil, notice de confidentialité RGPD, réinitialisation complète des données (droit à l'effacement), fenêtre À propos |
| 0.8.0 | 16/08/2026 | Jour 82 | Nouveau périmètre Bloc 4, absent du backlog V2 | Sonde `/health/ready`, `docs/supervision.md`, journalisation HTTP structurée + rotation, Dependabot, processus d'anomalies + correctif `ANO-001` |
| *non publié* | 19/08/2026 → 14/09/2026 | Jours 85–111 | Finalisation des docs BC04 + refonte US-10 | `docs/dependances.md`, `docs/recommandations-amelioration.md`, `docs/support-client-exemple.md` ; retouches profil/paramètres + barre d'onglets personnalisée ; onboarding reconstruit en assistant à 7 étapes (catégories, horizon fixe de 6 mois, choix de banque de démo) |

*(Le suivi de vélocité « 1 SP ≈ 1h15 » du plan V2 n'a pas été tenu après le Jour 1 — il n'existe pas de journal d'heures quotidien, donc les story points ne peuvent pas être reconstitués rétroactivement. Le temps calendaire écoulé et les versions livrées sont le seul signal honnête disponible.)*

---

## Audit V1 — Corrections appliquées

*(Historique — l'audit V1→V2 qui a produit le plan ci-dessous. Inchangé.)*

| # | Problème V1 | Correction V2 |
|---|---|---|
| 1 | S4 annonçait « 25 must + 17 should » — le total Must réel = 39 SP, infaisable | S4 rééquilibré : US-15 (CI/CD) et une partie de US-13/US-14 déplacées vers S3 |
| 2 | TECH-00 à 5 SP — sous-estimé pour la mise en place KMP iOS | TECH-00 réestimée à **8 SP** |
| 3 | Plan de test implicite — exigence RNCP C2.3.1 mal couverte | Story dédiée **US-17 Plan de test & correction de bugs** (3 SP) ajoutée en S4 |
| 4 | US-01 et US-02 fusionnées dans le découpage | Correctement séparées : US-01 = lecture, US-02 = écriture |
| 5 | Le dimanche « off » en réalité utilisé comme rattrapage | Dimanche formellement **tampon non engagé**, hors capacité |
| 6 | Cérémonies = 2h15/sprint, trop lourd | Réduites à **1h max/sprint** (planning 20min, revue 20min, rétro 20min) |

---

## Backlog produit (priorisé MoSCoW)

| ID | User Story | Fonctionnalité MVP | SP | Priorité | Statut (15/09/2026) | Note |
|---|---|---|---|---|---|---|
| US-01 | Suivre un défi d'épargne actif (lecture seule) | Défi actif | 5 | Must | ✅ Fait | Livré v0.1.0 |
| US-02 | Valider chaque jour du défi (écriture + série) | Défi actif | 5 | Must | ✅ Fait | Livré v0.1.0 |
| US-03 | Voir mes objectifs d'épargne | Objectif d'épargne | 5 | Must | ✅ Fait | Livré v0.1.0 |
| US-04 | Ajouter manuellement de l'argent à un objectif | Objectif d'épargne | 3 | Must | ✅ Fait | Livré v0.1.0 |
| US-05 | Créditer automatiquement un objectif depuis un défi | Objectif d'épargne | 3 | Must | ✅ Fait | Livré v0.1.0 |
| US-06 | Tableau de bord agrégé en écran d'accueil | Tableau de bord | 8 | Must | ✅ Fait | Livré v0.1.0 |
| US-07 | Navigation à 4 onglets en bas d'écran | Tableau de bord | 3 | Must | ✅ Fait | Livré v0.1.0 ; barre d'onglets reconstruite en composant maison (sept. 2026) |
| US-08 | Gagner des badges (6 badges initiaux) | Gamification | 5 | Must | ❌ Non fait | Aucun modèle/table `Badge` dans le code |
| US-09 | Série (streak) globale de l'application | Suivi d'habitudes | 3 | Must | ⚠️ Partiel | La logique de série existe sur le Tableau de bord/Défi ; non confirmée comme mécanisme « global » distinct inter-fonctionnalités |
| US-10 | Onboarding express en 4 écrans | Onboarding | 8 | Must | ✅ Fait (retravaillé) | v1 en v0.3.0 ; reconstruit en assistant à 7 étapes le 14/09/2026 |
| US-11 | Module d'apprentissage narratif | Apprentissage | 5 | Must | ✅ Fait | Livré v0.2.0 |
| US-12 | Rappel quotidien du défi (notification locale) | Rappels | 3 | Must | ⚠️ Partiel | Interrupteur d'interface uniquement (écran Profil) ; aucune planification `AlarmManager`/`UNUserNotificationCenter` trouvée |
| US-13 | Audit d'accessibilité OPQUAST + corrections | Accessibilité | 5 | Must (RNCP) | ⏳ Ouvert | Explicitement signalé comme non encore couvert dans `docs/recommandations-amelioration.md` (axe 2) |
| US-14 | Audit de sécurité OWASP + chiffrement local | Sécurité | 5 | Must (RNCP) | ⚠️ Partiel | JWT + BCrypt + validation des entrées livrés ; aucun `docs/security/owasp-mobile-audit.md` formel rédigé |
| US-15 | CI/CD complet (Android + iOS + tests + lint) | DevOps | 3 | Must (RNCP) | ⚠️ Partiel | La CI exécute les tests unitaires Android/JVM + les tests d'intégration serveur ; pas de runner iOS ; Detekt/Ktlint non branchés à la CI |
| US-16 | 3 manuels (déploiement, utilisateur, mise à jour) | Documentation | 5 | Must (RNCP) | ✅ Fait | `docs/deploiement.md`, `docs/utilisation.md`, `docs/mise-a-jour.md` |
| **US-17** | **Plan de test + plan de correction de bugs** | **Tests d'acceptation** | **3** | **Must (RNCP)** | ⏳ Ouvert | Tests automatisés + processus `docs/anomalies/` existants ; pas de `docs/acceptance-tests/test-plan.md` avec journal d'exécution Gherkin |
| TECH-00 | Mise en place KMP résiduelle (thème, BDD, Ktor, Koin, nav) | Fondation | 8 | Must | ✅ Fait | Livré v0.1.0 |
| TECH-01 | Chaîne CI minimale Sprint 1 | DevOps | 2 | Should | ✅ Fait | Livré v0.4.0 |
| TECH-02 | Mode démo + script + vidéo de secours | Préparation soutenance | 3 | Must | ⏳ Ouvert | Aucun artefact de mode démo / vidéo de secours trouvé |
| TECH-03 | ADR cumulatifs (8 fichiers et plus) | Documentation | _0_ | Continu | ❌ Non fait | Aucun dossier `docs/decisions/` ; décisions racontées dans `CHANGELOG.md`/`CONTEXT.md` à la place |
| **Total backlog** | | | **85 SP** | | | |
| **Total engagé en sprint** | | | **87 SP** | | | |

**Écart de 2 SP expliqué** *(tel que planifié)*. La différence vient de TECH-02 (mode démo) scindé 2+3 entre S1 (bouton debug) et S4 (mode démo complet), plus TECH-04/05/06/07 en Sprint 2 qui sont des découpages opérationnels des stories US-03/US-04/US-05 (déjà comptées dans le backlog). Écart attendu de la conversion story→tâche en Scrum.

**Note sur la vélocité** *(telle que planifiée — pas la façon dont elle a réellement été suivie ; voir le Résumé d'exécution)*. 1 SP ≈ 1h15 en moyenne effective pour la stack KMP avec tests + ADR + double build. Capacité 4 sprints × 26,5h = 106h → **~87 SP réalisables**. Le backlog tient **sans marge** : les tampons du dimanche (12h) absorbent la variance.

### Livré au-delà du backlog initial

Travail livré sans user story correspondante dans le plan V2 ci-dessus :

| Élément | Version | Valeur RNCP |
|---|---|---|
| Vrai serveur d'Open Banking simulé (Ktor + Exposed + PostgreSQL + JWT + BCrypt) | v0.5.0 | Dépasse le « remote mocké » ; fournit des artefacts réels pour C2.1/C2.2/C4 |
| Tests end-to-end de l'API avec Testcontainers | v0.5.0 | C2.2.2 |
| Bibliothèque de composants Wisepenny + spec de cohésion UX | v0.6.0 | Centralise le style — `docs/recommandations-amelioration.md` note que cela donne un effet de levier pour le chantier accessibilité encore ouvert |
| Analyse statique Detekt + Ktlint | v0.6.0 | C2.2.3 (maintenabilité) ; pas encore branché à la CI |
| Écran profil & confidentialité RGPD (réinitialisation des données, notice, À propos) | v0.7.0 | Conformité RGPD |
| Sondes readiness/liveness + journalisation HTTP structurée | v0.8.0 | C4.1.2 |
| Dependabot + doc de processus de mise à jour des dépendances | v0.8.0 | C4.1.1 |
| Processus de consignation des anomalies + template d'issue + `ANO-001` | v0.8.0 | C4.2.1 |
| Doc de recommandations d'amélioration | non publié | C4.3.1 |
| Doc d'exemple de support client | non publié | Bloc 4 |
| Onboarding reconstruit en assistant à 7 étapes (catégories, horizon fixe de 6 mois, choix de banque de démo) | non publié | Retravaille US-10 au-delà du périmètre initial à 4 écrans |

---

## Definition of Ready (DoR)

Une user story est « prête » à entrer dans un sprint si :
- [ ] Critères d'acceptation écrits en Gherkin (Given/When/Then)
- [ ] Story points estimés
- [ ] Dépendances techniques identifiées (remote mocké nécessaire ? composant partagé requis ?)
- [ ] Maquette Stitch ou wireframe rapide disponible
- [ ] Décisions de design tranchées (ou ADR à rédiger explicitement listée)

## Definition of Done (DoD)

Une user story est « terminée » si **toutes** les cases sont cochées :
- [ ] Code fusionné sur `main` via PR (auto-revue sérieuse même en solo)
- [ ] Tests unitaires sur les couches `domain` (use cases) + `data` (mappers) couvrant la story
- [ ] Builds Android **et** iOS au vert (vérifié par CI)
- [ ] Aucun warning de lint Kotlin non justifié
- [ ] `contentDescription` renseigné sur les nouveaux composants interactifs
- [ ] Test de fumée manuel : le parcours fonctionne hors-ligne après le premier fetch
- [ ] Captures d'écran ajoutées dans `docs/screenshots/`
- [ ] ADR rédigé si une décision technique non triviale a été prise
- [ ] **Scénario de test d'acceptation ajouté dans `docs/acceptance-tests/`** (couvre C2.3.1)
- [ ] Le critère Gherkin est démontrable

> **Réalisé (15/09/2026).** La clause « builds Android **et** iOS au vert (vérifié par CI) » n'a en réalité jamais été appliquée par la CI — `ci.yml` n'exécute que les tests unitaires Android/JVM et les tests d'intégration serveur, pas de runner iOS. Idem pour les clauses ADR et `docs/acceptance-tests/` : aucune story n'a réellement coché ces deux cases telles qu'écrites. Considérer cette DoD comme la cible visée, pas comme un relevé de ce qui a été vérifié story par story.

---

## Cérémonies allégées (60 min max / sprint)

| Cérémonie | Quand | Durée | Format |
|---|---|---|---|
| Planning de sprint | Lundi 9h | **20 min** | Validation du backlog + tâches du Jour 1 |
| Point quotidien | Chaque jour 9h | **5 min** | 3 lignes dans `daily-log.md` |
| Revue de sprint | Vendredi 17h | **20 min** | Vidéo de démo 1-2 min + mise à jour du tableau de bord |
| Rétrospective de sprint | Vendredi 17h20 | **20 min** | 3 colonnes start/stop/continue dans `retro-sX.md` |

**Refinement supprimé** — remplacé par 10 min en fin de Planning de sprint pour valider la DoR des stories du sprint en cours uniquement. Les stories des sprints futurs ont déjà une DoR pré-validée dans ce document.

> **Réalisé (15/09/2026).** Aucun fichier `daily-log.md` ni `retro-sX.md` n'existe dans le dépôt — cette cadence de cérémonies n'a pas été tenue telle qu'écrite. Les entrées de `CHANGELOG.md` et l'historique des commits en tiennent lieu de registre de fait.

---

# SPRINT 1 — Fondations & défi actif

**Jour 1 (lundi) → Jour 7 (dimanche).** Capacité engagée 26,5h (tampon dimanche). Engagement : **22 SP**.

> **Réalisé.** TECH-00, US-01, US-02 ont été livrées — et, en avance sur le planning, toute la surface CRUD de S2/S3 (Objectifs, Tableau de bord, navigation) a été livrée en même temps dans la `v0.1.0` le 02/06/2026, environ une semaine après le Jour 1. TECH-01 (CI) a suivi bien plus tard, dans la `v0.4.0` (14/07/2026, Jour 49). Le bouton debug de TECH-02 n'est pas confirmé dans le code actuel.

### Objectif du sprint

> Démontrer le défi actif « 7 jours sans café » de bout en bout, validable jour par jour, offline-first, sur Android et iOS. Établir le schéma architectural que les 3 prochains sprints répliqueront.

### Stories engagées

| ID | Story | SP |
|---|---|---|
| TECH-00 | Mise en place KMP résiduelle (thème, BDD, Ktor, Koin, nav) | 8 |
| US-01 | Suivre un défi d'épargne actif (lecture seule) | 5 |
| US-02 | Valider chaque jour + série | 5 |
| TECH-01 | Chaîne CI minimale (build + tests Android) | 2 |
| TECH-02 (partiel) | ADR-001 série stricte + bouton debug | 2 |
| **Total** | | **22 SP** |

### Critères d'acceptation Gherkin

**US-01 — Lecture du défi actif**
```gherkin
Scénario : Premier lancement avec réseau
  Étant donné que je lance l'application pour la première fois
  Quand le remote mocké renvoie le défi « 7 jours sans café »
  Alors je vois l'écran du défi avec 5/7 jours complétés
  Et les données sont persistées dans SQLDelight

Scénario : Relance hors-ligne
  Étant donné que j'ai déjà ouvert l'application au moins une fois
  Quand je relance après un kill complet
  Alors aucun appel réseau n'est effectué
  Et l'écran s'affiche en moins de 200ms

Scénario : Premier lancement sans réseau
  Étant donné que je n'ai aucune connexion
  Quand je lance l'application pour la première fois
  Alors je vois un message d'erreur clair avec un bouton réessayer
```

**US-02 — Validation du jour + série**
```gherkin
Scénario : Valider un jour
  Étant donné que je suis sur l'écran du défi au jour 6
  Quand j'appuie sur « Oui, +3€ épargnés »
  Alors le cercle du jour 6 devient vert
  Et le compteur « Épargné » augmente de 3€
  Et l'état persiste après un kill

Scénario : Idempotence
  Étant donné que j'ai déjà validé aujourd'hui
  Quand j'appuie de nouveau sur le bouton de validation
  Alors aucun double comptage ne se produit

Scénario : Série stricte
  Étant donné que ma série est de 5 jours
  Et que je n'ai pas validé hier
  Quand je valide aujourd'hui
  Alors ma série repart à 1 (ADR-001 : politique stricte)
```

### Découpage des tâches quotidiennes

**J1 Lundi — Mise en place résiduelle (5,5h visées, 8 SP)**
- [ ] Compléter `theme/` (Color, Typography, Shape, Spacing, WisepennyTheme) — 1,5h
- [ ] `data/local/database/wisepenny.sq` + driver SQLDelight Android + iOS — 1h
- [ ] `data/remote/api/HttpClientFactory.kt` avec Ktor MockEngine — 0,5h
- [ ] `di/AppModule.kt` Koin avec sous-modules vides — 0,5h
- [ ] `presentation/navigation/AppNavHost.kt` minimal — 0,5h
- [ ] **Build iOS au vert** (critique, à ne pas reporter) — 1h
- [ ] Test de bon sens SQLDelight cross-plateforme — 0,5h

**J2 Mardi — Domaine Challenge + tests (4,5h, 3 SP de US-01)**
- [ ] `domain/model/Challenge.kt`, `ChallengeProgress.kt`
- [ ] `domain/repository/ChallengeRepository.kt` (interface)
- [ ] `domain/usecase/challenge/GetActiveChallengeUseCase.kt`
- [ ] `domain/usecase/challenge/CalculateStreakUseCase.kt` (politique stricte)
- [ ] Tests : 4-5 tests de use case (série nominale, jour manqué, aujourd'hui seul, série de 3 jours)

**J3 Mercredi — Données Challenge + Repository (4,5h, 2 SP US-01 + 2 SP US-02)**
- [ ] Tables `challenge.sq` + `challenge_progress.sq`
- [ ] `data/local/dao/ChallengeDao.kt`
- [ ] `data/remote/dto/ChallengeDto.kt` + `data/remote/mock/ChallengeMockProvider.kt` (défi prédéfini)
- [ ] `data/mapper/ChallengeMapper.kt` + tests d'aller-retour
- [ ] `data/repository/ChallengeRepositoryImpl.kt` (stratégie first-fetch puis local)
- [ ] Bindings Koin

**J4 Jeudi — Présentation US-01 (4,5h, finalisation US-01)**
- [ ] `presentation/challenge/ChallengeViewModel.kt` + `ChallengeUiState.kt`
- [ ] `presentation/challenge/ActiveChallengeScreen.kt` reproduisant l'écran 3
- [ ] Composants extraits : `MintHeroCard`, `StatsRow`, `ProgressDots`
- [ ] États skeleton / succès / erreur
- [ ] **US-01 TERMINÉE** (toutes les cases DoD cochées)

**J5 Vendredi — Validation US-02 + idempotence (4,5h, finalisation US-02)**
- [ ] `domain/usecase/challenge/CompleteChallengeDayUseCase.kt` (idempotent)
- [ ] Action « Valider » câblée à l'écran
- [ ] Composant `HistoryRow`
- [ ] Tests d'idempotence + tests de ViewModel (3 transitions d'état)
- [ ] **Test de fumée iOS obligatoire**
- [ ] **US-02 TERMINÉE**
- [ ] Revue de sprint 17h + Rétro 17h20

**J6 Samedi — CI + ADR + finitions (4h, TECH-01 + TECH-02)**
- [ ] `.github/workflows/ci.yml` : build + tests Android
- [ ] Badge de statut dans le README
- [ ] `docs/decisions/ADR-001-strict-streak.md`
- [ ] Bouton debug caché (réinitialiser l'état du défi)
- [ ] Captures d'écran pour le manuel utilisateur
- [ ] Premier scénario renseigné dans `docs/acceptance-tests/test-plan.md`

**J7 Dimanche — TAMPON (non engagé)**
Si tout est au vert : repos. Sinon : rattrapage. Le tampon est votre soupape de sécurité.

### Risques S1

| Risque | Probabilité | Impact | Mitigation |
|---|---|---|---|
| Le build iOS bloque au Jour 1 | Moyenne | Élevé | Time-box de 2h au J1. Au-delà : `expect/actual` minimal, reporter Cocoapods |
| DI Koin mal configurée | Moyenne | Moyen | 1 module par couche, pas de scopes personnalisés |
| Vélocité surestimée | Moyenne | Élevé | Mesure honnête le vendredi, recalibrer S2 |

---

# SPRINT 2 — Objectifs d'épargne & intégration transverse

**J8 → J14.** Capacité 26,5h. Engagement **21 SP**.

> **Réalisé.** US-03/04/05 ont été livrées avec la `v0.1.0`, en même temps que S1, et non comme un sprint de semaine 2 distinct. TECH-04–07 (projection, sélecteur d'objectif, refactor S1, ADR-002) ne sont pas individuellement vérifiables dans le code ou les docs actuels — aucun fichier `docs/decisions/ADR-002*` n'existe.

### Objectif du sprint

> Permettre la consultation des objectifs, les contributions manuelles, et le crédit automatique défi → objectif. Démontrer la réplication réussie du schéma S1.

### Stories engagées

| ID | Story | SP |
|---|---|---|
| US-03 | Voir mes objectifs d'épargne | 5 |
| US-04 | Ajouter manuellement de l'argent à un objectif | 3 |
| US-05 | Crédit automatique défi → objectif | 3 |
| TECH-04 | Projection d'achèvement (moyenne mobile 30 jours) | 3 |
| TECH-05 | Bottom sheet sélecteur d'objectif | 3 |
| TECH-06 | Refactor S1 + tests d'intégration | 2 |
| TECH-07 | ADR-002 (règle 1 défi = 1 objectif) + test d'acceptation | 2 |
| **Total** | | **21 SP** |

### Critères Gherkin

**US-04**
```gherkin
Scénario : Contribution manuelle
  Étant donné que je suis sur l'écran détail « Lisbonne 2026 » à 450€/800€
  Quand j'appuie sur « Ajouter 10€ » et confirme
  Alors le montant épargné passe à 460€
  Et la barre de progression avance à 57,5%
  Et la projection se recalcule
```

**US-05**
```gherkin
Scénario : Crédit automatique
  Étant donné que j'ai un défi lié à « Lisbonne 2026 »
  Quand je valide un jour à 3€
  Alors une Contribution avec source=CHALLENGE de 3€ est créée
  Et le montant de Lisbonne augmente de 3€
```

### Découpage des tâches quotidiennes

**J8 Lundi — Domaine Objectif + tests (4,5h)**
- [ ] Modèles `SavingsGoal`, `Contribution` (enum source)
- [ ] Use cases : `GetGoals`, `GetGoalById`, `AddContribution`, `ProjectCompletionDate`
- [ ] Tests de use case (projection : 0€, montant=cible, moyenne mobile 30 jours)

**J9 Mardi — Données Objectif (4,5h)**
- [ ] Tables `goals.sq` + `contributions.sq` (FK)
- [ ] DAO + DTO + mock avec 3 objectifs cohérents avec le Tableau de bord
- [ ] Mappers + implémentation du repository
- [ ] Bindings Koin

**J10 Mercredi — Présentation Détail Objectif (4,5h)**
- [ ] `GoalDetailViewModel` + `GoalDetailScreen` (écran 4)
- [ ] Composants `GoalProgressCard`, `AmountInput`, `ContributionSheet`
- [ ] Validation du montant (regex, max)

**J11 Jeudi — Liste d'objectifs + sélecteur (4,5h)**
- [ ] `GoalListScreen`
- [ ] `GoalPickerSheet`
- [ ] Préparation du backlog S3 (10 min en fin de journée)

**J12 Vendredi — Intégration transverse (4,5h)**
- [ ] Refactor `CompleteChallengeDayUseCase` : crée une Contribution CHALLENGE
- [ ] Démarrage d'un nouveau défi → ouvre `GoalPickerSheet`
- [ ] Tests d'intégration (le use case défi vérifie l'effet sur l'objectif)
- [ ] **Test de fumée iOS**
- [ ] Revue de sprint + Rétro 17h

**J13 Samedi — Finitions + ADR + tests d'acceptation (4h)**
- [ ] ADR-002 : règle 1 défi = 1 objectif, comportement si l'objectif est supprimé
- [ ] Mesure de la couverture de tests (cible domaine 50%+)
- [ ] Scénarios de test d'acceptation (2-3 nouveaux)
- [ ] Captures d'écran

**J14 Dimanche — TAMPON**

### Risques S2

| Risque | Probabilité | Impact | Mitigation |
|---|---|---|---|
| Le refactor S1 casse les tests existants | Élevée | Moyen | Lancer la suite de tests AVANT ET APRÈS chaque commit |
| Cas limites de la projection (date passée, montant=0) | Moyenne | Moyen | Explicitement listés dans les tests dès J8 |

---

# SPRINT 3 — Tableau de bord, navigation, gamification & RNCP partiel

**J15 → J21.** Capacité 26,5h. Engagement **22 SP**.

> **Réalisé.** US-06/07 ont été livrées avec la `v0.1.0`, en même temps que S1/S2. US-08 (badges) n'a jamais été construite. US-09 (série) existe sur le Tableau de bord/Défi mais n'est pas confirmée comme mécanisme global distinct. US-15 (CI/CD) a été livrée partiellement dans la `v0.4.0` (14/07/2026) — tests Android/serveur uniquement, pas de runner iOS, pas d'étape Detekt/Ktlint en CI.

### Objectif du sprint

> Livrer l'écran d'accueil agrégé, la navigation à 4 onglets, les badges + le système de série global. **Démarrer le CI/CD complet et le chiffrement local** pour délester S4 (correction du déséquilibre V1).

### Stories engagées

| ID | Story | SP |
|---|---|---|
| US-06 | Tableau de bord agrégé | 8 |
| US-07 | Navigation à 4 onglets | 3 |
| US-08 | Système de badges | 5 |
| US-09 | Série globale | 3 |
| **US-15** | **CI/CD complet (avancé depuis S4)** | **3** |
| **Total** | | **22 SP** |

### Critères Gherkin

**US-06**
```gherkin
Scénario : Tableau de bord agrégé
  Étant donné que j'ai 3 objectifs et un défi actif au jour 5/7
  Quand j'ouvre l'application
  Alors je vois le total épargné ce mois-ci
  Et ma progression vers l'objectif mensuel
  Et le défi du jour dans une carte menthe
  Et mes 3 objectifs en défilement horizontal

Scénario : Deeplinks
  Quand j'appuie sur la carte du défi
  Alors je navigue vers ActiveChallengeScreen
  Quand j'appuie sur une carte d'objectif
  Alors je navigue vers GoalDetailScreen
```

**US-08**
```gherkin
Scénario : Premier badge
  Étant donné que je viens de terminer mon premier défi (jour 7 validé)
  Quand la validation est confirmée
  Alors « Premier défi terminé » se débloque
  Et une fenêtre modale s'affiche
  Et le badge apparaît dans le Profil
```

### Découpage des tâches quotidiennes

**J15 Lundi — Données Tableau de bord (4,5h)**
- [ ] `GetDashboardSnapshotUseCase` (agrège 5 indicateurs)
- [ ] Logique « Total épargné ce mois-ci »
- [ ] Tests

**J16 Mardi — UI Tableau de bord (4,5h)**
- [ ] `DashboardViewModel` + `DashboardScreen` (écran 1)
- [ ] Composants `MonthSavingsCard`, `StreakStrip`, `GoalCardHorizontal`, `DailyChallengeCard`
- [ ] Aucune valeur codée en dur

**J17 Mercredi — Navigation (4,5h)**
- [ ] `BottomNavBar` 4 onglets
- [ ] Migration des deeplinks S1-S2 vers la nav
- [ ] État sauvegardé entre onglets (rotation, arrière-plan)

**J18 Jeudi — Gamification (4,5h)**
- [ ] `Badge` + `BadgeType` (6 badges)
- [ ] Table `badges.sq`
- [ ] `EvaluateBadgesUseCase` (pattern observateur sur un Flow d'événements)
- [ ] `GetGlobalStreakUseCase`
- [ ] Tests : 1 par badge + 3 tests de série globale
- [ ] Branchement dans `CompleteChallengeDay` et `AddContribution`

**J19 Vendredi — UI Gamification + CI/CD (4,5h)**
- [ ] `BadgeUnlockedDialog`
- [ ] `ProfileScreen` v1
- [ ] **CI/CD : ajouter le build iOS dans GitHub Actions** (US-15 démarre ici)
- [ ] Revue de sprint + Rétro

**J20 Samedi — Finalisation CI iOS + ADR (4h)**
- [ ] CI : tests automatisés sur PR
- [ ] CI : lint Kotlin + Detekt
- [ ] ADR-003 (objectif mensuel = défini à l'onboarding, fixe)
- [ ] ADR-004 (série globale = ≥1 action par jour)
- [ ] Bouton debug : forcer le déblocage d'un badge
- [ ] Scénarios de test d'acceptation

**J21 Dimanche — TAMPON**

### Risques S3

| Risque | Probabilité | Impact | Mitigation |
|---|---|---|---|
| CI iOS sur GitHub Actions complexe (signature, certificats) | Élevée | Moyen | Garder un build iOS local + Android en CI si trop difficile. Documenter en ADR |
| Couplage spaghetti des badges | Élevée | Moyen | Pattern observateur/bus d'événements, pas d'appels directs |
| Sauvegarde de l'état des onglets | Moyenne | Élevé | Tester le cycle de vie complet avant fin de sprint |

---

# SPRINT 4 — Onboarding, apprentissage, rappels + livraison RNCP

**J22 → J28.** Capacité 26,5h. Engagement **22 SP**.

> **Réalisé.** US-10 a été livrée dans la `v0.3.0` (11/07/2026) — bien plus tôt que son créneau S4 d'origine — puis reconstruite en assistant à 7 étapes le 14/09/2026. US-11 a également été livrée plus tôt que prévu, dans la `v0.2.0` (25/06/2026). US-12 n'est qu'une interface (pas de planification). US-13 (accessibilité) et le document d'audit OWASP formel de US-14 sont toujours ouverts — voir `docs/recommandations-amelioration.md`. US-16 (3 manuels) est terminée. US-17 (doc de plan de test) n'a jamais été rédigée. TECH-02 (mode démo/vidéo de secours) n'a pas été trouvé dans le dépôt.

### Objectif du sprint

> Compléter les 3 fonctionnalités MVP restantes, passer les audits sécurité + accessibilité, finaliser la documentation et le plan de test, préparer la soutenance. **Vélocité réaliste grâce au rééquilibrage S3 → S4.**

### Stories engagées (équilibrées, plus de mensonges)

| ID | Story | SP |
|---|---|---|
| US-10 | Onboarding express en 4 écrans | 8 |
| US-11 | Module d'apprentissage narratif | 5 |
| US-12 | Rappel quotidien du défi | 3 |
| **US-17** | **Plan de test complet + plan de correction de bugs** | **3** |
| TECH-02 (suite) | Mode démo + script + vidéo de secours | 3 |
| **Intégré** | US-13/14/16 réparties sur S4 (voir découpage) | — |
| **Total** | | **22 SP** |

**Note honnête.** US-13 (accessibilité), US-14 (sécurité), US-16 (manuels) sont **réparties en parallèle** du travail sur les fonctionnalités les J22-J24 plutôt que comptées en SP supplémentaires : sécurité = 2h en J23, accessibilité = 2h en J24, manuels = une journée complète J26. Le total d'heures (~10h) est intégré dans le découpage et compté dans les 26,5h.

### Critères Gherkin

**US-10**
```gherkin
Scénario : Premier lancement
  Étant donné une installation neuve
  Quand je lance l'application
  Alors je vois l'écran de bienvenue de l'onboarding

Scénario : Fin de l'onboarding
  Étant donné que j'ai renseigné prénom, premier objectif, montant mensuel
  Quand j'appuie sur « Terminer »
  Alors je vois le Tableau de bord avec mon prénom
  Et mon objectif initial apparaît dans la liste
  Et `onboarding_completed = true` est persisté
```

**US-11**
```gherkin
Scénario : Lecture d'un module
  Étant donné que le module « Livret d'épargne » a 6 sections
  Quand je commence la lecture
  Et que je quitte en milieu de section 3
  Quand je reviens plus tard
  Alors je reprends à la section 3
  Quand j'appuie sur « J'ai terminé » en fin de lecture
  Alors le module est marqué comme terminé
  Et le badge « Premier module lu » se débloque
```

### Découpage des tâches quotidiennes (honnêtement séquencé)

**J22 Lundi — Onboarding (5h max, 8 SP)**
- [ ] `data/local/preferences/UserPreferences.kt` (expect/actual : Datastore Android / Keychain iOS)
- [ ] `UserProfile` + use cases (`SaveUserProfile`, `IsOnboardingCompleted`, `CreateInitialGoal`)
- [ ] 4 écrans : Bienvenue, Prénom, Premier objectif, Montant mensuel
- [ ] Routage conditionnel dans `AppNavHost`
- [ ] Prénom dynamique sur Tableau de bord + Profil
- [ ] **Chiffrement Datastore activé dès maintenant** (couvre partiellement US-14)

**J23 Mardi — Apprentissage + Sécurité (5h)**

*Matin Apprentissage (3h)*
- [ ] `composeResources/files/modules/savings-account.json` (5 sections rédigées)
- [ ] Modèles `LearningModule`, `ModuleSection`
- [ ] Use cases de lecture/complétion
- [ ] Table `module_progress.sq`
- [ ] `LearningListScreen` + `ModuleReaderScreen`
- [ ] Branchement du badge « Premier module lu »
- [ ] ADR-005 (contenu JSON statique : RGPD + éco-conception)

*Après-midi Sécurité (2h, US-14)*
- [ ] Validateurs d'entrée (`AmountValidator`, `NameValidator`, `DateValidator`)
- [ ] Politique de logs : `BuildConfig.DEBUG` partout, aucune donnée personnelle en release
- [ ] `docs/security/owasp-mobile-audit.md` rédigé (1 ligne par item du Top 10 : applicable / non / mitigé)

**J24 Mercredi — Rappels + Accessibilité (5h)**

*Matin Rappels (2h, US-12)*
- [ ] `expect class NotificationScheduler` + actuals (AlarmManager / UNUserNotificationCenter)
- [ ] `ScheduleDailyChallengeReminderUseCase`
- [ ] Interrupteur dans le Profil
- [ ] Permission Android 13+ correctement demandée
- [ ] **Si dérive au-delà de 2h30 : couper. Rappel = quotidien uniquement, suffisant pour le MVP**

*Après-midi Accessibilité (3h, US-13)*
- [ ] Audit OPQUAST : `docs/accessibility/opquast-audit.md` (~30 règles pertinentes documentées)
- [ ] Corrections : `contentDescription`, cibles tactiles 48dp+, ordre de focus
- [ ] Test réel TalkBack sur Tableau de bord + Défi actif + Détail objectif
- [ ] Captures avant/après pour 2 corrections significatives

**J25 Jeudi — Plan de test + plan de bugs (4,5h, US-17)**
- [ ] Compléter `docs/acceptance-tests/test-plan.md` : 20 scénarios couvrant les Must-have
- [ ] Exécution manuelle de tous les scénarios, statuts consignés (OK/KO)
- [ ] `docs/acceptance-tests/bug-correction-plan.md` : 1 fiche par bug détecté (analyse + correctif proposé + statut)
- [ ] Corriger les bugs bloquants détectés (time-box 2h)
- [ ] Mesure finale de la couverture de tests (cible ≥50% domaine)

**J26 Vendredi — Documentation complète (5h, US-16)**
- [ ] `docs/manuals/deployment-manual.md` (prérequis, commandes, signature, distribution)
- [ ] `docs/manuals/user-manual.md` (parcours utilisateur avec toutes les captures cumulées)
- [ ] `docs/manuals/update-manual.md` (versionnage sémantique, processus de release, changelog)
- [ ] Nettoyer les 5-8 ADR
- [ ] README final avec section « Stack & arguments RNCP »
- [ ] Revue de sprint + Rétro sur l'ensemble du projet

**J27 Samedi — Préparation de la soutenance (4h, TECH-02)**
- [ ] Mode démo : bouton Profil pour réinitialiser des états prédéfinis (neuf, J5, badge, objectif à 80%)
- [ ] Script de démo chronométré 8-10 min
- [ ] **Vidéo de secours enregistrée** sur l'appareil cible (parcours complet)
- [ ] Bloc de 3 slides : méthodologie 4 sprints, vélocité réelle, ADR comme preuve d'arbitrage
- [ ] Planification de la rétrospective finale (livrable C3.1)

**J28 Dimanche — Répétition + livraison (TAMPON ENGAGÉ ce sprint)**
- [ ] 2 répétitions complètes chronométrées
- [ ] Passage visuel final vs charte graphique
- [ ] Tag `v1.0.0`, build de release final
- [ ] Sauvegarde du dépôt + docs sur disque externe

**Note J28.** Exceptionnellement, le dimanche est engagé en S4 car c'est le dernier jour avant la livraison. C'est votre dernier filet de sécurité — utilisez-le.

### Plan de réduction du périmètre en cas de dérive (mis à jour)

Du moins critique au plus critique :
1. **Captures avant/après accessibilité** → texte uniquement. -0,5 SP
2. **Module d'apprentissage : 5 sections au lieu de 6-7** → garder le mécanisme. -1 SP
3. **CI iOS** → Android uniquement en CI, build iOS local documenté en ADR. -1 SP
4. **2e répétition** → n'en garder qu'une. -1 SP

**Ne jamais couper :** OWASP, OPQUAST de base, plan de test, manuels, mode démo, vidéo de secours.

> **Résultat réel de ce plan de coupe (15/09/2026).** La coupe n°3 (CI iOS) s'est produite par défaut plutôt que par décision — iOS n'a jamais été branché dans `ci.yml`. Les coupes n°1 et n°2 ne s'appliquent pas encore puisque le travail d'accessibilité et le nombre de sections du module d'apprentissage n'ont jamais été réexaminés au regard de ce plan. Les éléments formellement « à ne jamais couper » — doc d'audit OWASP, audit OPQUAST, plan de test, mode démo, vidéo de secours — sont justement ceux qui restent en suspens ; voir **État actuel & travail restant** ci-dessous.

### Risques S4

| Risque | Probabilité | Impact | Mitigation |
|---|---|---|---|
| Les rappels KMP (notif expect/actual) plus complexes que 2h | Élevée | Moyen | Time-box strict, sinon Android uniquement + ADR |
| L'audit OWASP révèle une vulnérabilité majeure | Faible | Élevé | Audit J23, 2 jours pour corriger |
| Bug bloquant dans le plan de test J25 | Moyenne | Élevé | 2h de correctif dédié J25, sinon plan B = documenter comme bug connu |
| La démo plante en soutenance | Moyenne | Élevé | Vidéo de secours obligatoire J27 |

---

## État actuel & travail restant (au 15/09/2026)

D'après `docs/recommandations-amelioration.md` (BC04, auto-évalué) et les écarts identifiés tout au long de ce document :

1. **Accessibilité (US-13, critique RNCP).** Aucun audit RGAA/OPQUAST réalisé pour l'instant. La bibliothèque de composants Wisepenny désormais centralisée (v0.6.0) fait qu'une seule passe sur les composants partagés devrait se propager partout — signalé comme à fort effet de levier, 5–8 jours-homme.
2. **Document d'audit sécurité formel (US-14, critique RNCP).** JWT, BCrypt et validation des entrées sont implémentés, mais il n'existe pas de document écrit mappant le Top 10 OWASP pour le dossier du jury.
3. **Plan de tests d'acceptation (US-17, critique RNCP).** Des tests automatisés existent et s'exécutent en CI ; un `docs/acceptance-tests/test-plan.md` écrit en Gherkin avec un statut d'exécution par scénario n'a pas été produit.
4. **iOS en CI.** `ci.yml` n'a jamais reçu de runner iOS/macOS — la clause de DoD « builds Android et iOS au vert (vérifié par CI) » n'a été appliquée par la CI pour aucune story. Signalé comme axe 4 dans `docs/recommandations-amelioration.md`.
5. **Mode démo & vidéo de secours (TECH-02).** Introuvable dans le code ou les docs — encore nécessaire avant la soutenance.
6. **ADR sous forme de fichiers (TECH-03).** Les décisions existent sous forme narrative dans `CHANGELOG.md`/`CONTEXT.md`, pas sous la forme des fichiers `docs/decisions/ADR-*.md` initialement engagés.
7. **Badges (US-08) et rappels planifiés (US-12).** Reportés ; non construits au-delà d'un interrupteur d'interface pour les rappels.
8. **Long terme, non bloquant** (d'après `docs/recommandations-amelioration.md`) : connexion bancaire réelle (axe 1), déploiement conteneurisé avec moniteur d'uptime réel (axe 3), synchronisation hors-ligne/résolution de conflits (axe 5) — tous explicitement cadrés comme post-MVP.

---

## Tableau de bord du projet (à tenir à jour)

> **Note (15/09/2026).** Ce tableau hebdomadaire SP/heures n'a jamais été renseigné — le projet n'a pas suivi une cadence de sprint hebdomadaire dans la réalité. Voir la **Chronologie réelle des versions** en début de document pour le registre réel.

| Sprint | SP engagés | SP terminés | Vélocité | Heures réelles / budget | Objectif de sprint ✓ |
|---|---|---|---|---|---|
| S1 | 22 | _____ | _____ | _____ / 26,5h | _____ |
| S2 | 21 | _____ | _____ | _____ / 26,5h | _____ |
| S3 | 22 | _____ | _____ | _____ / 26,5h | _____ |
| S4 | 22 | _____ | _____ | _____ / 26,5h | _____ |
| **Total** | **87** | _____ | _____ | _____ / 106h | _____ |

**Note sur la vélocité** *(telle que planifiée)*. 87 SP × 1,22h/SP ≈ 106h. Tient exactement dans la capacité engageable. Les dimanches J7, J14, J21 sont un pur tampon (12h supplémentaires disponibles en cas de dérive). J28 est engagé pour les répétitions finales.

Livrable RNCP direct pour **C3.2.1** (suivi de l'avancement) et **C3.2.2** (arbitrages).

---

## Mapping RNCP → Réalisé (mis à jour le 15/09/2026)

| Critère | Statut | Artefact |
|---|---|---|
| C2.1.1, C2.1.2 (environnements, CI) | ⚠️ Partiel | `.github/workflows/ci.yml` — tests unitaires Android/JVM + tests d'intégration serveur Testcontainers ; pas de runner iOS |
| C2.2.1 (prototype) | ✅ | Application KMP fonctionnelle Android/iOS/desktop depuis la `v0.1.0` |
| C2.2.2 (tests unitaires) | ⚠️ Partiel | 3 fichiers de tests côté client (`StartupScreenTest`, `MapperTest`, `GoalRepositoryAutoSaveTest`) + 1 fichier de tests d'intégration côté serveur (`ApiTest`) ; pas de mesure de couverture formelle |
| C2.2.3 (sécurité, accessibilité, maintenabilité) | ⚠️ Partiel | Sécurité : JWT + BCrypt + validation des entrées livrés, pas de document d'audit OWASP formel. Accessibilité : non encore couverte — axe ouvert dans `docs/recommandations-amelioration.md`. Maintenabilité : Detekt + Ktlint configurés mais non appliqués en CI |
| C2.2.4 (déploiement progressif) | ⚠️ Partiel | Chaîne CI existante ; pas de déploiement conteneurisé/par étapes (axe 3 ouvert dans `docs/recommandations-amelioration.md`) |
| C2.3.1, C2.3.2 (tests d'acceptation, bugs) | ⚠️ Partiel | Tests automatisés + processus `docs/anomalies/` (`ANO-001`) couvrent le suivi des bugs ; pas encore de `docs/acceptance-tests/test-plan.md` avec journal d'exécution Gherkin |
| C2.4.1 (documentation) | ✅ | `docs/deploiement.md`, `docs/utilisation.md`, `docs/mise-a-jour.md` |
| C3.1 (planification) | ✅ | Ce document (plan initial + bilan d'exécution) |
| C3.2.1 (suivi) | ⚠️ Partiel | `CHANGELOG.md` + historique Git tiennent lieu de registre de suivi de fait ; les `daily-log.md`/`retro-sX.md` initialement prévus n'ont pas été tenus |
| C3.2.2 (arbitrages) | ✅ | Le Résumé d'exécution de ce document — bascule mock→backend réel, insertion du périmètre Bloc 4, extension du planning |
| C3.4.1, C3.4.2 (suivi client, démo) | ⏳ En attente | Script de démo + vidéo de secours (TECH-02) pas encore produits |
| C4.1.1 (mise à jour des dépendances) | ✅ | `docs/dependances.md`, `.github/dependabot.yml` |
| C4.1.2 (supervision) | ✅ | `docs/supervision.md`, routes `/health` et `/health/ready` |
| C4.2.1 (consignation des anomalies) | ✅ | `docs/anomalies/processus.md`, `ANO-001` |
| C4.3.1 (recommandations d'amélioration) | ✅ | `docs/recommandations-amelioration.md` |
| Bloc 4 (support client) | ✅ | `docs/support-client-exemple.md` |

**C3.3 (équipe).** Projet solo, à adapter en soutenance : contexte solo justifié + accessibilité OPQUAST comme proxy handicap + maîtrise de la communication via les ADR et les rétros écrites.

---

## Engagements pré-Jour 1 (à figer formellement)

- [x] Politique de série : stricte (ADR-001)
- [x] ~~Remote mocké = backend final~~ ⚠️ **Dépassé le 22/07/2026 (`v0.5.0`)** — voir le Résumé d'exécution : un vrai serveur d'Open Banking simulé a été construit à la place
- [x] Pas de tests UI Compose (domain+data uniquement)
- [x] 1 module d'apprentissage livré
- [x] Format d'apprentissage : narratif uniquement
- [x] Pas de design au-delà des 4 écrans Stitch
- [x] Notifications locales uniquement
- [x] Rappels réductibles si J24 dérive
- [x] **CI iOS réductible à Android uniquement si J19-J20 dérivent**
- [x] **Dimanche = tampon non engagé (sauf J28)**

---

## Validation finale

| Vérification | Statut (tel que planifié) | Statut (15/09/2026) |
|---|---|---|
| Arithmétique SP cohérente (22+21+22+22 = 87) | ✅ | N/A — le suivi SP n'a pas été tenu après le Jour 1 |
| Capacité en heures cohérente (4×26,5 = 106h) | ✅ | N/A — aucun journal d'heures quotidien n'existe |
| Vélocité réaliste (87 SP ≈ 106h à 1,22h/SP) | ✅ | ❌ Le temps réel écoulé représente ~4× les 28 jours planifiés, et le projet est toujours en cours |
| Aucun sprint surchargé (engagement max = 22 SP) | ✅ | N/A |
| C2.3.1 (plan de test) explicitement couvert | ✅ | ⏳ En attente — tests automatisés + processus d'anomalies existants, doc de plan de test Gherkin formel non rédigé |
| Tampon existant (dimanches sauf J28) | ✅ | N/A — le modèle de tampon hebdomadaire n'a pas été utilisé en pratique |
| Plan de coupe documenté pour S4 | ✅ | ⚠️ Non exercé tel qu'écrit ; les écarts réels (accessibilité, CI iOS) sont documentés à la place dans `docs/recommandations-amelioration.md` |
| Risques identifiés par sprint | ✅ | ✅ Conservés comme artefact de planification historique |
| Engagements pré-Jour 1 figés | ✅ | ⚠️ Un engagement (remote mocké = backend final) a été réexaminé et dépassé |

**Plan validé le 25/05/2026. Prêt à démarrer le Jour 1 lundi matin.** *(Bilan d'exécution ajouté le 15/09/2026 — projet toujours en cours ; voir État actuel & travail restant ci-dessus pour ce qu'il reste à faire.)*
