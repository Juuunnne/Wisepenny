# Processus de mise à jour des dépendances

> Livrable BC04 – C4.1.1 : *« Gérer les mises à jour des dépendances et des
> bibliothèques tiers, en surveillant régulièrement les nouvelles versions, en
> évaluant les impacts des mises à jour, et en les intégrant de manière sécurisée
> pour maintenir l'application à jour et sécurisée. »*

Ce document décrit **le processus** de mise à jour. La procédure technique de montée
de version sans casse (invariants, migrations) est détaillée dans le
[manuel de mise à jour](mise-a-jour.md).

## 1. Périmètre logiciel concerné

Toutes les dépendances sont centralisées dans le **catalogue de versions**
[`gradle/libs.versions.toml`](../gradle/libs.versions.toml), ce qui donne un point
unique de mise à jour. Le périmètre couvre :

| Famille | Exemples | Criticité |
|---------|----------|-----------|
| Plateforme mobile | Kotlin, Compose Multiplatform, AGP, AndroidX Lifecycle | Élevée |
| Persistance | SQLDelight (mobile), Exposed + HikariCP + PostgreSQL (serveur) | Élevée |
| Serveur / réseau | Ktor (client & serveur), kotlinx-serialization | Élevée |
| Sécurité | JWT, BCrypt | **Critique** |
| Injection / outils | Koin, kotlinx-coroutines, kotlinx-datetime | Moyenne |
| Qualité / tests | JUnit, Testcontainers, Detekt, Ktlint | Faible |
| CI | Actions GitHub (`checkout`, `setup-java`, …) | Moyenne |

## 2. Surveillance des nouvelles versions

La veille est **automatisée** par **Dependabot**
([`.github/dependabot.yml`](../.github/dependabot.yml)), qui surveille deux
écosystèmes :

- `gradle` — les dépendances du catalogue de versions ;
- `github-actions` — les actions de la chaîne d'intégration continue.

Dependabot ouvre automatiquement des **pull requests** de montée de version. Il
signale aussi les **alertes de sécurité** (avis de vulnérabilité) sur les
dépendances, traitées en priorité.

## 3. Fréquence et type de mise à jour

| Type de mise à jour | Déclenchement | Traitement |
|---------------------|---------------|------------|
| Correctifs de sécurité | Alerte Dependabot (dès publication) | Prioritaire, fusion rapide après CI verte |
| Versions **mineures / patch** | Hebdomadaire (lundi), **regroupées** en une PR | Revue puis fusion si CI verte |
| Versions **majeures** | Hebdomadaire, **une PR isolée par dépendance** | Évaluation d'impact manuelle (voir §4) |

Le regroupement des mises à jour mineures/patch limite le bruit ; l'isolement des
montées majeures permet de les évaluer une par une. La mise à jour est donc
**semi-automatique** : la détection et l'ouverture des PR sont automatiques, la
**décision de fusion reste manuelle** et conditionnée à la CI.

## 4. Évaluation de l'impact avant intégration

Chaque PR de mise à jour est évaluée avant fusion :

1. **La CI rejoue l'ensemble des tests** (unitaires mobile + intégration serveur via
   Testcontainers). Une CI rouge bloque la fusion.
2. **Lecture des notes de version** pour les changements de comportement (*breaking
   changes*), en particulier sur les briques critiques (Ktor, Exposed, SQLDelight, JWT).
3. **Montées majeures** : vérification manuelle des points sensibles — migrations de
   schéma (voir [manuel de mise à jour](mise-a-jour.md)), compatibilité API, sécurité.
4. **Intégration sécurisée** : fusion uniquement après CI verte ; en cas de doute, la
   PR est testée sur une branche avant fusion sur `main`.

## 5. Traçabilité

Les montées de version notables sont reportées dans le
[journal de version](../CHANGELOG.md), et chaque PR de mise à jour reste consultable
dans l'historique GitHub (liée à son passage de CI).
