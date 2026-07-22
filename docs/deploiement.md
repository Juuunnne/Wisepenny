# Manuel de déploiement — Wisepenny

Ce document décrit comment construire et lancer les deux composants du projet :
l'application mobile Kotlin Multiplatform (Android et iOS) et le backend Ktor
(API open banking **simulée**). Toutes les commandes s'exécutent depuis la racine
du dépôt, sauf mention contraire.

L'API est simulée : le serveur ne se connecte à aucune banque réelle et sert des
données de démonstration.

---

## 1. Prérequis

| Outil | Version | Usage |
| --- | --- | --- |
| JDK | 17 | Requis pour exécuter Gradle et l'Android Gradle Plugin 8.11.2, et pour compiler le module `:server` (cible `JVM_17`). |
| Android Studio | récent (avec Android SDK) | Build et exécution de l'app Android ; fournit un JBR utilisable comme `JAVA_HOME`. |
| Xcode | récent (macOS) | Compilation et exécution de l'app iOS. |
| Docker | récent | Lancement de PostgreSQL en conteneur pour le backend. |

Le dépôt embarque le wrapper Gradle (`./gradlew`), aucune installation de Gradle
n'est nécessaire.

> En ligne de commande, `./gradlew` a besoin d'un `JAVA_HOME` pointant vers un JDK 17.
> Par exemple, le JBR fourni par Android Studio convient :
> `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`

---

## 2. Application Android

Le module mobile est `:composeApp`. Configuration lue depuis
[composeApp/build.gradle.kts](../composeApp/build.gradle.kts) et
[gradle/libs.versions.toml](../gradle/libs.versions.toml) :

- `applicationId` / `namespace` : `com.example.wisepenny`
- `minSdk` : 26, `compileSdk` : 36, `targetSdk` : 36
- `versionName` : `1.0`, `versionCode` : 1

### Build debug

```bash
./gradlew :composeApp:assembleDebug
```

Artefact produit :
`composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### Build release

```bash
./gradlew :composeApp:assembleRelease
```

Artefact produit :
`composeApp/build/outputs/apk/release/composeApp-release-unsigned.apk`

Remarques importantes sur le build release :

- Aucune configuration de signature n'est déclarée : l'APK release est **non signé**
  (`-unsigned`). La signature est une étape ultérieure, hors du périmètre actuel.
- La minification R8 est **désactivée** (`isMinifyEnabled = false`). Son activation
  est planifiée, non vérifiable en l'état.

### Exécution

Le plus simple est d'ouvrir le projet dans Android Studio et de lancer la
configuration `composeApp` sur un émulateur ou un appareil (Android 8.0 / API 26
minimum), ou d'installer l'APK debug via `adb install`.

---

## 3. Application iOS

Les cibles iOS sont `iosArm64` (appareil) et `iosSimulatorArm64` (simulateur).
Le framework partagé s'appelle `ComposeApp`.

### Validation de la chaîne iOS

Pour vérifier que le framework partagé compile sans passer par Xcode :

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Exécution

Ouvrir le dossier [iosApp/](../iosApp) dans Xcode, sélectionner un simulateur,
puis lancer. Xcode déclenche la construction du framework Kotlin et l'assemble
avec le point d'entrée SwiftUI.

---

## 4. Backend Ktor + PostgreSQL

Le module serveur est `:server` (Ktor sur moteur Netty). Il nécessite une base
PostgreSQL accessible.

### 4.1. Lancer PostgreSQL en conteneur

Les valeurs par défaut lues dans
[server/src/main/resources/application.conf](../server/src/main/resources/application.conf)
attendent une base `wisepenny`, un utilisateur `wisepenny` et le mot de passe
`wisepenny` sur le port `5432` :

```bash
docker run --name wisepenny-postgres \
  -e POSTGRES_DB=wisepenny \
  -e POSTGRES_USER=wisepenny \
  -e POSTGRES_PASSWORD=wisepenny \
  -p 5432:5432 \
  -d postgres:16
```

Au démarrage, le serveur crée automatiquement les tables manquantes
(`SchemaUtils.createMissingTablesAndColumns`, voir
[DatabaseFactory.kt](../server/src/main/kotlin/com/wisepenny/server/db/DatabaseFactory.kt)).
Aucune étape de migration manuelle n'est requise pour un premier démarrage.

### 4.2. Lancer le serveur

```bash
./gradlew :server:run
```

Le serveur écoute par défaut sur `http://localhost:8080`.

En environnement `dev` (valeur par défaut), un jeu de données de démonstration est
inséré au premier démarrage sur une base vide (1 utilisateur, 2 comptes, ~200
transactions, 2 objectifs), défini dans
[DevSeeder.kt](../server/src/main/kotlin/com/wisepenny/server/db/DevSeeder.kt).
Identifiants de démonstration : `demo@wisepenny.fr` / `demo1234`.

---

## 5. Variables d'environnement

Le serveur lit sa configuration depuis `application.conf`, chaque valeur pouvant
être surchargée par une variable d'environnement :

| Variable | Rôle | Valeur par défaut |
| --- | --- | --- |
| `PORT` | Port d'écoute HTTP du serveur | `8080` |
| `DB_JDBC_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/wisepenny` |
| `DB_USER` | Utilisateur de la base | `wisepenny` |
| `DB_PASSWORD` | Mot de passe de la base | `wisepenny` |
| `JWT_SECRET` | Secret HMAC de signature des jetons JWT | `dev-only-insecure-secret-change-me-in-production` |
| `APP_ENV` | Environnement applicatif (`dev` ou `prod`) | `dev` |

### Avertissements de sécurité et d'exploitation

- **`JWT_SECRET` doit impérativement être surchargé** en dehors du poste local
  (CI, conteneur, production). La valeur par défaut est un secret de développement
  volontairement non sûr : ne jamais s'y fier hors machine de développement.
- **`APP_ENV=prod` désactive le seeder de démonstration**. À utiliser dès que la
  base ne doit pas recevoir les données factices.

---

## 6. Vérification post-déploiement

1. **Sonde de vivacité** :

   ```bash
   curl -s http://localhost:8080/health
   # → {"status":"UP"}
   ```

2. **Obtention d'un jeton** (environnement `dev`, données de démonstration) :

   ```bash
   curl -s -X POST http://localhost:8080/auth/token \
     -H "Content-Type: application/json" \
     -d '{"email":"demo@wisepenny.fr","password":"demo1234"}'
   # → {"accessToken":"...","expiresIn":3600}
   ```

3. **Appel d'une route protégée** avec le jeton obtenu :

   ```bash
   curl -s http://localhost:8080/accounts \
     -H "Authorization: Bearer <accessToken>"
   # → liste des comptes de l'utilisateur
   ```

Le détail complet des routes figure dans [API_SPEC.md](../API_SPEC.md).
