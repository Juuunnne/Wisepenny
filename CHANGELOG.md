# Journal des versions — Wisepenny

Toutes les évolutions notables de l'application sont consignées dans ce fichier.

Le format s'appuie sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),
et le projet suit le [versionnage sémantique](https://semver.org/lang/fr/).

Catégories utilisées : **Ajouts** (nouvelles fonctionnalités), **Modifications**
(changements de comportement existant), **Corrections** (corrections d'anomalies),
**Suppressions** (retraits de code ou de fonctionnalités).

---

## [0.8.0] - 2026-08-16 — Supervision, dépendances & traitement des anomalies

### Ajouts
- Sonde de disponibilité (*readiness*) `GET /health/ready` : vérifie l'accès à
  PostgreSQL (`SELECT 1`) et renvoie **503** si la base est injoignable.
- Documentation du système de supervision et d'alerte (`docs/supervision.md`) :
  périmètre, sondes, indicateurs de suivi, seuils d'alerte et modalité de signalement.
- Journalisation structurée des requêtes HTTP (méthode, chemin, statut, durée) via
  le greffon CallLogging, avec rotation des journaux à 30 jours (`logback.xml`).
- Surveillance automatisée des dépendances par Dependabot (`.github/dependabot.yml`) :
  écosystèmes Gradle et actions GitHub, cadence hebdomadaire, regroupement des
  montées mineures et correctives.
- Formulaire structuré de consignation d'anomalie
  (`.github/ISSUE_TEMPLATE/anomalie.yml`) à champs obligatoires garantissant la
  reproductibilité.
- Processus documenté de collecte et de traitement des anomalies
  (`docs/anomalies/processus.md`) avec grille de sévérité.

### Corrections
- **ANO-001** — Prévention de régression du clignotement de l'assistant d'onboarding
  au démarrage : extraction de la décision de routage en fonction pure `startupScreen`
  et ajout d'un test unitaire de non-régression exécuté par la CI. Voir
  `docs/anomalies/ANO-001-onboarding-flash.md`.

## [0.7.0] - 2026-07-27 — Profil, confidentialité & maîtrise des données

### Ajouts
- Écran de profil : motivation, date d'inscription et actions sur les données.
- Notice de confidentialité (RGPD) : minimisation des données et stockage local.
- Réinitialisation complète des données avec confirmation (droit à l'effacement).
- Fenêtre « À propos » (version de l'application, cadre du projet).
- Possibilité de revoir le parcours d'introduction sans réinitialiser les données.

## [0.6.0] - 2026-07-24 — Design system & qualité de code

### Ajouts
- Bibliothèque de composants d'interface Wisepenny (design system unifié).
- Spécification de cohésion UX documentant les règles d'interface.
- Promotion du défi quotidien et parcours d'acceptation du défi sur le tableau de bord.
- Intégration de Detekt et Ktlint pour l'analyse statique et le formatage du code.

### Modifications
- Migration de l'ensemble des écrans vers les composants Wisepenny.
- Configuration de Detekt pour analyser correctement les modules Kotlin Multiplatform
  et ajustement des règles (TODO, `@Preview`).

### Suppressions
- Retrait de la documentation `DesignSystem.md` (remplacée par la spec de cohésion UX).

## [0.5.0] - 2026-07-22 — Backend & API bancaire simulée

### Ajouts
- Module serveur Ktor (squelette, plugins, monitoring des requêtes).
- Persistance PostgreSQL via l'ORM Exposed.
- Authentification JWT et points d'entrée de l'API REST.
- Spécification de l'API (`API_SPEC.md`).
- Tests d'intégration end-to-end de l'API avec Testcontainers.
- Ajout des tests d'intégration serveur à la chaîne d'intégration continue.
- Manuels de déploiement et d'utilisation.
- Prénom de l'utilisateur dans le profil pour la personnalisation du tableau de bord.

### Modifications
- Le client de test API ignore désormais les clés JSON inconnues (robustesse).
- Mise à jour de la configuration de build.

### Suppressions
- Retrait du composable `ComingSoonScreen`.

## [0.4.0] - 2026-07-14 — Intégration continue & tests

### Ajouts
- Chaîne d'intégration continue (GitHub Actions) exécutant les tests unitaires.
- Tests unitaires de la logique d'auto-épargne et des mappers.
- Abstraction multiplateforme `BackHandler` pour la gestion du retour arrière.

### Suppressions
- Retrait du fichier `CONTEXT.md`.

## [0.3.0] - 2026-07-11 — Onboarding

### Ajouts
- Modèle, dépôt et ViewModel du profil d'onboarding.
- Interface et écran de profil d'onboarding.
- Parcours d'onboarding complet et politique de geste retour par plateforme.

## [0.2.0] - 2026-06-25 — Module d'apprentissage

### Ajouts
- Modèles, dépôts, mappers, DTO et schéma SQL des modules d'apprentissage.
- Interface et ViewModel des écrans d'apprentissage, intégrés à la navigation.
- Activation des ressources Compose ; ajustements de navigation et SQLDelight.
- Amorçage (seed) de la progression initiale du module « bourse ».

## [0.1.0] - 2026-06-02 — Fondations

### Ajouts
- Projet initial Kotlin Multiplatform (Android / iOS / desktop).
- Dépendances Ktor, Koin, Serialization et SQLDelight.
- Domaine Challenge : modèle, dépôt, schéma SQLDelight et drivers de base de données.
- Fonctionnalité Objectifs (Goals) : modèle, dépôt, interface et SQL.
- Tableau de bord : interface, navigation et base de données des contributions.
- Injection de dépendances Koin et branchement du ViewModel sur toutes les plateformes.
- Navigation Compose et amorçage de données (DataSeeder).

### Suppressions
- Retrait des fichiers modèles multiplateformes (`Platform` / `Greeting`) générés.

[0.8.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.8.0
[0.7.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.7.0
[0.6.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.6.0
[0.5.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.5.0
[0.4.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.4.0
[0.3.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.3.0
[0.2.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.2.0
[0.1.0]: https://github.com/Juuunnne/Wisepenny/releases/tag/v0.1.0
