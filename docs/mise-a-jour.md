# Manuel de mise à jour — Wisepenny

Ce document décrit les règles à suivre pour faire évoluer le projet sans casser les
données existantes ni les invariants du domaine : évolution des schémas de base de
données, montée de version des dépendances et versionnage des livraisons.

---

## 1. Évolution du schéma mobile (SQLDelight)

Le schéma de la base locale est défini par les fichiers `.sq` sous
[composeApp/src/commonMain/sqldelight/com/wisepenny/db/](../composeApp/src/commonMain/sqldelight/com/wisepenny/db/)
(`Profile.sq`, `Goal.sq`, `Contribution.sq`, `Challenge.sq`, `ModuleProgress.sq`).
La base générée s'appelle `WisepennyDatabase`
([composeApp/build.gradle.kts](../composeApp/build.gradle.kts)).

**Règle de migration.** Toute évolution de schéma se fait par un **script de migration
`.sqm` versionné**, jamais par une modification destructive du schéma existant :

- les fichiers `.sqm` sont nommés par numéro de version croissant (`1.sqm`, `2.sqm`,
  …) et déposés à côté des fichiers `.sq` ;
- SQLDelight applique les migrations dans l'ordre lorsque la version de la base
  installée sur l'appareil est inférieure à la version cible ;
- on **n'édite jamais** de façon destructive une table déjà livrée (pas de `DROP`
  d'une colonne portant des données utilisateur) : on ajoute une migration qui fait
  évoluer le schéma tout en préservant les données ;
- le fichier `.sq` reflète l'état final du schéma ; le `.sqm` décrit le chemin d'une
  version à la suivante.

> État actuel : le schéma en est à sa première version et aucun fichier `.sqm` n'a
> encore été nécessaire. La règle ci-dessus s'applique dès la première évolution de
> schéma livrée à des utilisateurs.

---

## 2. Invariant du domaine : les montants

Cet invariant est **non négociable** et doit être respecté par tout code qui manipule
de l'argent :

- côté **mobile**, les montants sont stockés et manipulés en **centimes**, en `Long`
  (voir par exemple `amountCents` dans
  [OnboardingViewModel.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/onboarding/OnboardingViewModel.kt)) ;
- côté **serveur**, les montants sont stockés en `DECIMAL(12,2)`
  (voir [Tables.kt](../server/src/main/kotlin/com/wisepenny/server/db/Tables.kt)) et
  transitent sur le réseau sous forme de **chaînes de caractères** (jamais de nombre
  JSON), voir [Dtos.kt](../server/src/main/kotlin/com/wisepenny/server/dto/Dtos.kt) ;
- **jamais de type flottant** (`Float` / `Double`) pour représenter un montant, à
  aucune étape. Le flottant introduit des erreurs d'arrondi inacceptables pour de la
  monnaie.

Toute nouvelle fonctionnalité manipulant des montants doit s'aligner sur cette règle.

---

## 3. Migration du schéma serveur

Au démarrage, le serveur exécute
`SchemaUtils.createMissingTablesAndColumns` (voir
[DatabaseFactory.kt](../server/src/main/kotlin/com/wisepenny/server/db/DatabaseFactory.kt)).

- Cette opération est **idempotente** : elle crée les tables et colonnes manquantes
  et laisse le reste intact.
- Elle n'est **pas versionnée** : elle ne gère ni les renommages, ni les
  suppressions, ni les transformations de données.

**Conséquence pour la production.** Cette approche suffit à un contexte de
démonstration mais n'est pas adaptée à un déploiement réel. Une montée en production
nécessitera un outil de migration versionnée (**Flyway** ou **Liquibase**) pour
tracer et rejouer les évolutions de schéma de façon déterministe.

---

## 4. Montée de version d'une dépendance

Toutes les versions sont centralisées dans le catalogue
[gradle/libs.versions.toml](../gradle/libs.versions.toml). C'est le **seul** endroit
où modifier un numéro de version.

Procédure :

1. Repérer la référence de version dans la section `[versions]` du catalogue (par
   exemple `ktor`, `kotlin`, `exposed`, `sqlDelight`).
2. Modifier la valeur à cet endroit unique ; toutes les dépendances qui pointent vers
   cette référence (`version.ref = "..."`) sont mises à jour ensemble.
3. Reconstruire et relancer les tests des deux modules pour valider la compatibilité :

   ```bash
   ./gradlew :composeApp:testDebugUnitTest
   ./gradlew :server:test
   ```

4. Ne monter qu'une dépendance (ou un groupe cohérent) à la fois, pour isoler toute
   régression.

Certaines versions sont couplées : par exemple le plugin et le dialecte SQLDelight
partagent la même référence `sqlDelight`, et les plugins Compose/Kotlin suivent la
référence `kotlin`. Les faire évoluer de concert.

---

## 5. Versionnage et pose de tag

Le projet suit le **versionnage sémantique** `MAJEUR.MINEUR.CORRECTIF` :

- **MAJEUR** : changement incompatible (rupture d'API ou de schéma) ;
- **MINEUR** : ajout de fonctionnalité rétrocompatible ;
- **CORRECTIF** : correction rétrocompatible.

La version du module serveur est déclarée dans
[server/build.gradle.kts](../server/build.gradle.kts) (`version = "0.1.0"`).

Pour livrer une version :

1. Mettre à jour le numéro de version si nécessaire.
2. Poser un **tag annoté** correspondant sur `main` :

   ```bash
   git tag -a v0.1.0 -m "Périmètre de la version"
   git push origin v0.1.0
   ```

3. Créer la release correspondante sur GitHub, en listant les fonctionnalités
   livrées et les limites connues.
