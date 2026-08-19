# Processus de collecte et de consignation des anomalies

> Livrable BC04 – C4.2.1 : *« Consigner les anomalies détectées en élaborant un
> processus de collecte et consignation, en utilisant des outils de collecte et en
> y intégrant toutes les informations pertinentes, afin de déterminer le correctif
> à mettre en place. »*

## 1. Outil de collecte

Les anomalies sont consignées dans **GitHub Issues**, via un **formulaire structuré**
([`.github/ISSUE_TEMPLATE/anomalie.yml`](../../.github/ISSUE_TEMPLATE/anomalie.yml)).
Le formulaire impose les champs nécessaires pour reproduire et qualifier le bogue :
résumé, sévérité, environnement, étapes de reproduction, résultat attendu / obtenu,
puis analyse et préconisation.

Le choix de GitHub Issues est cohérent avec la typologie du projet : le code, la CI et
le suivi des anomalies vivent au même endroit, chaque anomalie peut être reliée au
**commit correctif** et au **passage de CI** qui la clôt.

Pour le dossier, chaque anomalie significative est aussi archivée sous forme de **fiche
Markdown** dans `docs/anomalies/` (identifiant `ANO-xxx`), afin d'en conserver une trace
lisible et versionnée indépendamment de la plateforme.

## 2. Cycle de vie d'une anomalie

| Étape | Description | Sortie |
|-------|-------------|--------|
| 1. Détection | Bogue observé (test manuel, retour utilisateur, CI en échec). | — |
| 2. Consignation | Ouverture d'une fiche via le formulaire → informations de reproduction. | Fiche `ANO-xxx` |
| 3. Qualification | Analyse de la cause, sévérité et priorité. | Cause + préconisation |
| 4. Correction | Développement du correctif sur une branche dédiée. | Commit correctif |
| 5. Vérification | La CI rejoue les tests ; ajout d'un test de non-régression si pertinent. | CI verte |
| 6. Clôture | Fiche clôturée, correctif documenté dans le [journal de version](../../CHANGELOG.md). | Entrée CHANGELOG |

## 3. Champs d'une fiche de consignation

Chaque fiche contient au minimum :

- **Identifiant** (`ANO-xxx`) et **résumé**.
- **Sévérité** : Critique / Majeure / Mineure.
- **Environnement** : plateforme, version, appareil / OS.
- **Étapes de reproduction** numérotées.
- **Résultat attendu** vs **résultat obtenu**.
- **Analyse de la cause racine**.
- **Préconisation puis correctif appliqué** (avec le commit).
- **Prévention de régression** (test, revue, garde-fou).

## 4. Qualification de la sévérité

| Sévérité | Critère | Délai de traitement visé |
|----------|---------|--------------------------|
| Critique | Perte de données, application inutilisable, faille de sécurité | Immédiat |
| Majeure | Fonctionnalité principale dégradée ou incorrecte | Avant la version suivante |
| Mineure | Gêne visuelle, cas limite sans perte de données | Planifiée |

## 5. Fiches consignées

| ID | Résumé | Sévérité | État |
|----|--------|----------|------|
| [ANO-001](ANO-001-onboarding-flash.md) | L'assistant d'onboarding clignote au démarrage pour un utilisateur déjà inscrit | Mineure | ✅ Corrigée |
