# Recommandations d'amélioration

> Livrable BC04 – C4.3.1 : *« Proposer des axes d'amélioration en prenant en compte
> les indicateurs de performance et en analysant les retours utilisateurs afin de
> maintenir et renforcer l'attractivité du logiciel. »*

Ces recommandations s'appuient sur l'état actuel du projet (backend simulé, application
multiplateforme), sur les indicateurs de supervision définis dans
[`supervision.md`](supervision.md) et sur les retours utilisateurs (dont l'anomalie
[ANO-001](anomalies/ANO-001-onboarding-flash.md)). Chaque axe est chiffré en
**charge indicative** (jours-homme) et en **priorité**.

## Synthèse

| # | Axe d'amélioration | Gain attendu | Charge (j-h) | Priorité |
|---|--------------------|--------------|:------------:|:--------:|
| 1 | Connexion bancaire réelle (Open Banking / DSP2) | Attractivité : passage du simulé au réel | 15–20 | 🔴 Haute |
| 2 | Conformité accessibilité (RGAA / bonnes pratiques) | Élargit le public, exigence réglementaire | 5–8 | 🔴 Haute |
| 3 | Déploiement conteneurisé + supervision réelle | Disponibilité, exploitabilité | 4–6 | 🟠 Moyenne |
| 4 | Tests UI et intégration iOS dans la CI | Réduction des régressions | 3–5 | 🟠 Moyenne |
| 5 | Mode hors-ligne et synchronisation | Confort d'usage, rétention | 8–12 | 🟡 Basse |

## Axe 1 — Connexion bancaire réelle (Open Banking / DSP2)

- **Constat** : le backend est aujourd'hui **simulé** (données de démonstration). C'est
  la principale limite fonctionnelle du produit.
- **Recommandation** : intégrer un agrégateur bancaire conforme **DSP2** (ex : Bridge,
  Powens) pour récupérer soldes et transactions réels, derrière la même API interne.
- **Gain** : transforme la démo en produit réellement utilisable → forte hausse de
  l'attractivité et de la valeur perçue.
- **Coût / délai** : 15–20 j-h ; nécessite un compte agrégateur et une revue sécurité
  (données bancaires). **Réaliste** car l'architecture isole déjà l'accès données
  derrière des services.

## Axe 2 — Conformité accessibilité

- **Constat** : aucun référentiel d'accessibilité n'est encore formellement couvert.
- **Recommandation** : appliquer les bonnes pratiques (contrastes, tailles de cibles,
  libellés pour lecteurs d'écran, navigation clavier) et se mesurer à un référentiel
  (**RGAA** / OPQUAST).
- **Gain** : élargit le public (personnes en situation de handicap) et répond à une
  attente réglementaire.
- **Coût / délai** : 5–8 j-h, essentiellement sur les composants Wisepenny déjà
  centralisés (donc **effet de levier** : corriger une fois, bénéficier partout).

## Axe 3 — Déploiement conteneurisé + supervision réelle

- **Constat** : la supervision est spécifiée mais pas déployée (voir
  [`supervision.md`](supervision.md), §5).
- **Recommandation** : conteneuriser le serveur (Docker) et brancher un moniteur
  d'uptime réel sur `/health/ready`, avec alertes.
- **Gain** : garantit la disponibilité et rend le service exploitable en continu.
- **Coût / délai** : 4–6 j-h ; le serveur lit déjà sa config via variables
  d'environnement, ce qui facilite la conteneurisation.

## Axe 4 — Tests UI et intégration iOS dans la CI

- **Constat** : la CI couvre les tests unitaires (logique métier partagée) mais **pas
  les tests iOS** (absence de *runner* macOS) ni les tests d'interface.
- **Recommandation** : ajouter un *runner* macOS pour les tests iOS et quelques tests
  d'interface Compose sur les parcours critiques (onboarding, création d'objectif).
- **Gain** : réduit le risque de régression sur les parcours clés — dans la lignée de
  la prévention mise en place pour [ANO-001](anomalies/ANO-001-onboarding-flash.md).
- **Coût / délai** : 3–5 j-h ; coût récurrent modéré (minutes de *runner* macOS).

## Axe 5 — Mode hors-ligne et synchronisation

- **Constat** : l'application dépend de la disponibilité du serveur.
- **Recommandation** : tirer parti de la base locale SQLDelight comme cache et gérer
  la synchronisation (résolution de conflits) au retour de connexion.
- **Gain** : confort d'usage et meilleure rétention.
- **Coût / délai** : 8–12 j-h (la gestion des conflits est la partie sensible).

## Priorisation

À court terme, les axes **1** (valeur produit) et **2** (accessibilité, à fort effet de
levier) sont les plus rentables. Les axes **3** et **4** sécurisent l'exploitation et la
qualité. L'axe **5** est un confort à traiter une fois le socle réel en place.
