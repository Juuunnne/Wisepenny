# ANO-001 — Clignotement de l'assistant d'onboarding au démarrage

> Fiche de consignation d'anomalie — voir le [processus](processus.md).
> Livrables BC04 : **C4.2.1** (consignation) et **C4.2.2** (traitement du correctif).

| Champ | Valeur |
|-------|--------|
| **Identifiant** | ANO-001 |
| **Résumé** | L'assistant d'onboarding s'affiche brièvement au démarrage pour un utilisateur déjà inscrit. |
| **Sévérité** | Mineure (gêne visuelle, aucune perte de données) |
| **État** | ✅ Corrigée |
| **Détectée le** | 2026-07-11, lors des tests manuels du parcours d'onboarding |
| **Composant** | Application cliente — point d'entrée `App.kt` |

## Environnement

- **Plateforme** : Android / iOS / Desktop (comportement commun, code partagé)
- **Version app** : 0.3.0 (introduction de l'onboarding)
- **Zone concernée** : démarrage à froid de l'application

## Étapes de reproduction

1. Compléter l'onboarding une première fois, puis fermer complètement l'application.
2. Relancer l'application **à froid**.
3. Observer le tout premier rendu de l'écran.

## Résultat attendu

L'utilisateur déjà inscrit arrive **directement** sur le tableau de bord, sans
jamais revoir l'assistant d'onboarding.

## Résultat obtenu

L'assistant d'onboarding apparaît **pendant une fraction de seconde** (un ou deux
rendus) avant d'être remplacé par le tableau de bord — un « flash » visuel.

## Analyse de la cause racine

Au démarrage, le profil utilisateur est lu de façon **asynchrone** depuis la base
locale (SQLDelight) et exposé sous forme de flux. Le point d'entrée `App()`
décidait quel écran afficher à partir de deux états seulement :

- profil présent et `onboardingCompleted == true` → application ;
- sinon → assistant d'onboarding.

Le problème : tant que la première lecture de la base n'est pas terminée, le flux
émet sa **valeur initiale** (`null`, c.-à-d. « pas de profil »). Ce cas était traité
comme « utilisateur non inscrit » et déclenchait donc l'affichage de l'assistant.
L'état **« lecture en cours »** n'était pas distingué de l'état **« lecture terminée,
aucun profil »** — d'où le flash le temps que la base réponde.

**Cause racine** : absence d'un état de chargement explicite (modélisation d'état
incomplète au démarrage).

## Correctif appliqué

Introduction d'un état **tri-valué** `ProfileLoad` distinguant les trois situations :

```kotlin
private sealed interface ProfileLoad {
    data object Loading : ProfileLoad            // lecture DB en cours
    data class Loaded(val profile: Profile?) : ProfileLoad  // lecture terminée
}
```

Rendu au démarrage :

- **`Loading`** → un fond neutre plein écran (aucun assistant affiché) ;
- **`Loaded`** → décision définitive : tableau de bord si `onboardingCompleted`,
  sinon assistant d'onboarding.

La valeur initiale du flux est désormais `ProfileLoad.Loading` (et non plus `null`),
ce qui supprime la fenêtre pendant laquelle l'assistant pouvait s'afficher à tort.

- **Commit correctif** : `79c6700` — *« Add onboarding flow & platform back-gesture policy »*
- **Fichier** : [`composeApp/src/commonMain/kotlin/com/wisepenny/App.kt`](../../composeApp/src/commonMain/kotlin/com/wisepenny/App.kt)

## Vérification et prévention de régression

- **Vérification manuelle** : après correctif, un démarrage à froid d'un utilisateur
  inscrit affiche directement le tableau de bord, sans flash.
- **Prévention de régression (test automatisé)** : la logique de décision au démarrage
  a été extraite en une fonction pure `internal fun startupScreen(load: ProfileLoad)`
  dans [`App.kt`](../../composeApp/src/commonMain/kotlin/com/wisepenny/App.kt), couverte
  par [`StartupScreenTest`](../../composeApp/src/commonTest/kotlin/com/wisepenny/StartupScreenTest.kt).
  Le test verrouille le point sensible de l'anomalie : **tant que le profil est en
  cours de lecture, l'écran routé est `LOADING`, jamais l'assistant**. Ce test est
  exécuté à chaque `push`/PR par la CI (`.github/workflows/ci.yml`,
  tâche `:composeApp:testDebugUnitTest`) — le correctif tire donc profit du processus
  d'intégration continue.

## Traçabilité

- Journal de version : voir la section **Corrections** de [CHANGELOG.md](../../CHANGELOG.md).
