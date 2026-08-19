# Résolution d'un problème en collaboration avec le support client

> Livrable BC04 – C4.3.3 : *« Collaborer avec les équipes de support, en fournissant
> une expertise technique, en répondant aux retours clients, en résolvant des
> problèmes complexes afin d'améliorer le logiciel. »*
>
> **Nature de la mise en situation.** Wisepenny est un projet de fin d'études,
> développé en solo avec un backend simulé. Le référentiel autorisant la mise en
> situation **fictive**, le scénario ci-dessous décrit un dispositif réaliste
> support ↔ développement et les rôles de chaque partie prenante. Le comportement
> logiciel décrit, lui, correspond au **code réel** du calcul d'auto-épargne.

## 1. Contexte du retour client

- **Canal** : un utilisateur pilote signale un problème via le support.
- **Message** : *« J'ai programmé une épargne automatique de 10 € par semaine sur mon
  objectif "Vacances". Je suis parti 3 semaines sans ouvrir l'application. À mon
  retour, je m'attendais à voir 3 semaines d'épargne d'un coup, mais je ne suis pas
  sûr que tout ait été pris en compte. »*
- **Ressenti** : doute sur la fiabilité du calcul → risque de perte de confiance.

## 2. Qualification par le support

Le **support** consigne le retour via la [fiche de consignation](anomalies/processus.md)
et collecte les informations de reproduction :

- objectif avec auto-épargne hebdomadaire de 10 € ;
- dernière application il y a 21 jours ;
- application ouverte à J+21.

Ne pouvant trancher sur le comportement attendu, le support **escalade au
développement** pour expertise technique.

## 3. Analyse technique (expertise développement)

Vérification dans le code du calcul d'auto-épargne
([`GoalRepositoryImpl.applyDueAutoSaves`](../composeApp/src/commonMain/kotlin/com/wisepenny/data/repository/GoalRepositoryImpl.kt)) :

- le nombre de périodes échues est calculé à l'ouverture :
  `périodes = jours_écoulés / cadence` → ici `21 / 7 = 3` ;
- les **3 semaines sont bien rattrapées en une fois** (3 × 10 € = 30 €), dans la
  limite du montant restant pour atteindre l'objectif.

Ce comportement est **couvert par un test automatisé**
([`GoalRepositoryAutoSaveTest.appliesMultiplePeriodsAtOnce`](../composeApp/src/androidUnitTest/kotlin/com/wisepenny/data/repository/GoalRepositoryAutoSaveTest.kt)) :
le calcul est donc **correct**. Le problème n'est pas un bogue de calcul mais un
**manque de visibilité** : l'utilisateur ne voit aucune trace expliquant le rattrapage.

## 4. Résolution apportée

- **Réponse immédiate au client** (via le support) : confirmation que les 3 semaines
  ont bien été épargnées en une fois au retour, avec l'explication du fonctionnement du
  rattrapage.
- **Amélioration produit** (axe de fond) : afficher dans l'historique une **ligne de
  contribution datée par période rattrapée** (« Épargne auto — semaine du … »), pour
  rendre le rattrapage visible et lever le doute. Les contributions sont déjà
  enregistrées côté données ; l'amélioration porte sur leur **restitution dans
  l'interface**.

## 5. Contribution des différentes parties prenantes

| Partie prenante | Contribution |
|-----------------|--------------|
| **Utilisateur pilote** | Signale le doute et fournit le contexte d'usage. |
| **Support client** | Consigne le retour, collecte les infos de reproduction, escalade, puis répond au client. |
| **Développement** | Analyse le code et les tests, confirme la justesse du calcul, identifie la cause réelle (visibilité) et propose l'amélioration. |
| **Product owner** | Priorise l'amélioration d'affichage dans la feuille de route (voir [recommandations](recommandations-amelioration.md)). |

## 6. Enseignement

Un retour client formulé comme un « bogue » s'est révélé être un **problème
d'expérience utilisateur** : le calcul était juste, mais silencieux. La collaboration
support ↔ développement a permis de **distinguer l'anomalie réelle de la perception**
et d'orienter la correction vers la bonne cause.
