# Manuel d'utilisation — Wisepenny

Ce document décrit le parcours utilisateur de l'application mobile, écran par écran,
tel qu'il est réellement implémenté dans
[composeApp/src/commonMain/kotlin/com/wisepenny/presentation/](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/).

## Nature des données et de l'API

- L'application stocke ses données **localement sur l'appareil**, via une base
  SQLDelight embarquée (`WisepennyDatabase`). Aucune donnée n'est envoyée sur un
  serveur distant depuis l'application mobile.
- Le backend Ktor du projet expose une **API open banking simulée** : il ne s'agit
  d'aucune connexion bancaire réelle. À ce stade, l'application mobile ne consomme
  pas encore cette API ; les deux composants coexistent et sont documentés séparément
  (voir [API_SPEC.md](../API_SPEC.md)).
- Les chiffres affichés (comptes, objectifs, défis, contributions) proviennent d'un
  **jeu de démonstration** généré localement au premier lancement.

---

## 1. Premier lancement — l'onboarding

Au démarrage, l'application lit le profil local
([App.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/App.kt)). Tant que
l'onboarding n'est pas terminé, elle affiche l'assistant de première configuration ;
une fois terminé, elle bascule sur l'application principale.

L'assistant compte sept étapes plein écran, dans cet ordre
([OnboardingViewModel.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/onboarding/OnboardingViewModel.kt)) :

1. **Bienvenue** — écran d'accueil et présentation.
2. **Devise** — choix de la devise parmi `EUR`, `USD`, `GBP` (EUR par défaut).
3. **Motivation** — raison d'épargner, parmi : Voyager, Un gros achat, Me faire
   plaisir, Une voiture, Un logement, Un projet perso. Ce choix fixe la catégorie
   et l'icône de l'objectif, sans sélecteur séparé.
4. **Objectif** — saisie du nom de l'objectif et du montant cible (en unités
   entières). La saisie ne peut se poursuivre que si le nom est renseigné et le
   montant supérieur à zéro.
5. **Aperçu** — choix de l'échéance (6, 12 ou 24 mois). L'écran calcule la
   contribution mensuelle nécessaire et la date de fin projetée.
6. **Notifications** — proposition d'activer les notifications (étape facultative,
   bouton « passer »).
7. **Banque** — proposition de liaison bancaire (**simulée**, étape facultative).
   Aucune connexion réelle n'est établie.

À la validation de la dernière étape, l'application :

- génère le monde de démonstration local (comptes, transactions, défis) ;
- crée l'objectif prioritaire de l'utilisateur avec sa date cible et une épargne
  automatique mensuelle déduite de l'échéance choisie ;
- enregistre le profil (`onboardingCompleted = true`), ce qui fait basculer
  l'application vers l'écran principal.

---

## 2. Navigation principale

Une fois l'onboarding terminé, l'application présente une barre d'onglets basse à
quatre entrées
([AppNavHost.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/navigation/AppNavHost.kt)) :

| Onglet | Écran | Contenu |
| --- | --- | --- |
| Accueil | Tableau de bord | Synthèse de l'épargne, objectifs et défis en cours |
| Apprendre | Modules d'apprentissage | Liste des modules pédagogiques |
| Objectifs | Objectifs d'épargne | Liste des objectifs et leur progression |
| Profil | Profil utilisateur | Informations de compte et réinitialisation |

Les écrans de détail (objectif, défi, lecture d'un module) s'ouvrent par-dessus, en
plein écran.

---

## 3. Tableau de bord (Accueil)

Le tableau de bord
([DashboardViewModel.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/dashboard/DashboardViewModel.kt))
regroupe :

- un en-tête de salutation personnalisée avec les initiales de l'utilisateur ;
- le montant épargné sur le mois en cours et sa variation par rapport au mois
  précédent ;
- la progression vers l'objectif mensuel ;
- une série (streak) d'assiduité ;
- la liste des objectifs, chacun avec sa barre de progression ;
- les défis actifs, avec le jour courant et le pourcentage d'avancement.

Un appui sur un objectif ou un défi ouvre son écran de détail.

> Note : certaines valeurs du tableau de bord (série d'assiduité, objectif mensuel)
> sont des données fixes de démonstration, correspondant à des fonctionnalités
> assumées comme non encore implémentées.

---

## 4. Objectifs

L'onglet Objectifs liste les objectifs d'épargne avec leur progression. Un appui
ouvre l'écran de détail
([GoalViewModel.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/goal/GoalViewModel.kt)),
qui permet :

- d'ajouter rapidement un montant à l'objectif ;
- d'ajouter un montant manuel ;
- de configurer une épargne automatique ;
- de consulter les défis liés à l'objectif.

L'objectif prioritaire est celui créé pendant l'onboarding. La création de nouveaux
objectifs depuis l'application (bouton « + ») est prévue pour une étape ultérieure.

---

## 5. Défis

L'écran de détail d'un défi
([ChallengeViewModel.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/challenge/ChallengeViewModel.kt))
présente le défi actif et permet de **valider** ou de **passer** la journée en cours.
La progression se reflète ensuite sur le tableau de bord.

---

## 6. Modules d'apprentissage (Apprendre)

L'onglet Apprendre liste les modules pédagogiques
([LearningViewModel.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/learning/LearningViewModel.kt)).
Un appui ouvre le lecteur de module, page par page ; l'avancement dans les pages est
mémorisé pour reprendre la lecture là où elle s'est arrêtée.

---

## 7. Profil

L'onglet Profil
([ProfileViewModel.kt](../composeApp/src/commonMain/kotlin/com/wisepenny/presentation/profile/ProfileViewModel.kt))
affiche la motivation choisie et la date d'inscription.

Il propose une action de **réinitialisation des données** : celle-ci efface toutes
les tables locales (objectifs, contributions, défis, progression, profil). Cette
opération correspond au droit à l'effacement (RGPD) et, en vidant le profil, ramène
l'application à l'assistant d'onboarding.

---

## Données de démonstration

Le jeu de démonstration est généré localement au premier lancement (après
l'onboarding) et sert de contenu de départ pour tous les écrans. La réinitialisation
depuis le profil permet de repartir d'un état vierge et de rejouer l'onboarding.
