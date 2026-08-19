# Système de supervision et d'alerte — Wisepenny

> Livrable BC04 – C4.1.2 : *« Concevoir un système de supervision et d'alerte en
> déterminant le périmètre de supervision et en identifiant les indicateurs de
> suivi pertinents, en mettant en place des sondes, en configurant la modalité des
> signalements afin de garantir une disponibilité permanente du logiciel. »*

## 1. Périmètre de supervision

Wisepenny est une application Kotlin Multiplatform composée de deux briques :

| Composant | Rôle | Supervisé ? |
|-----------|------|-------------|
| Serveur Ktor (`server/`) | API REST, authentification, accès données | ✅ Oui — cœur de la disponibilité |
| Base PostgreSQL | Persistance des comptes, objectifs, transactions | ✅ Oui — via la sonde de disponibilité |
| Application cliente (Android / iOS / desktop) | Interface utilisateur | ⚠️ Supervision applicative (rapports de plantage) — voir §5 |

Le **point critique de disponibilité** est le couple serveur + base : si l'un des
deux tombe, l'application cliente ne peut plus se synchroniser. La supervision se
concentre donc en priorité sur cette brique.

## 2. Sondes mises en place

Deux sondes HTTP sont exposées par le serveur (voir
[`HealthRoutes.kt`](../server/src/main/kotlin/com/wisepenny/server/routes/HealthRoutes.kt)) :

| Sonde | Endpoint | Finalité | Réponse |
|-------|----------|----------|---------|
| **Vivacité** (*liveness*) | `GET /health` | Le processus serveur répond-il ? | `200 {"status":"UP"}` |
| **Disponibilité** (*readiness*) | `GET /health/ready` | La base PostgreSQL est-elle joignable ? (`SELECT 1`) | `200` si OK, **`503`** si la base est injoignable |

La distinction liveness / readiness suit la convention des orchestrateurs
(Kubernetes, Docker Swarm) :

- une sonde de **vivacité** qui échoue ⇒ **redémarrer** l'instance ;
- une sonde de **disponibilité** qui échoue ⇒ **retirer l'instance du trafic**
  (load balancer) sans la tuer, le temps que la dépendance (base) revienne.

Une troisième source de supervision complète les sondes : la **journalisation
structurée des requêtes** (`CallLogging`, une ligne par appel HTTP — voir
[`Monitoring.kt`](../server/src/main/kotlin/com/wisepenny/server/plugins/Monitoring.kt))
et la configuration [`logback.xml`](../server/src/main/resources/logback.xml). Ces
journaux alimentent le suivi des erreurs et des temps de réponse.

## 3. Indicateurs de suivi et seuils d'alerte

Critères de qualité et de performance retenus, adaptés à une API bancaire simulée :

| Indicateur | Source | Seuil d'alerte | Criticité |
|-----------|--------|----------------|-----------|
| Disponibilité du serveur | `GET /health` | Pas de `200` pendant > 1 min | 🔴 Critique |
| Disponibilité de la base | `GET /health/ready` | Réponse `503` | 🔴 Critique |
| Temps de réponse API | Journaux `CallLogging` | p95 > 800 ms sur 5 min | 🟠 Majeur |
| Taux d'erreurs 5xx | Journaux `CallLogging` | > 2 % des requêtes sur 5 min | 🟠 Majeur |
| Erreurs 4xx anormales (401/403) | Journaux `CallLogging` | Pic inhabituel | 🟡 À surveiller (sécurité) |

Ces seuils sont volontairement stricts pour une application manipulant des données
financières, où l'indisponibilité et la lenteur dégradent directement la confiance.

## 4. Modalité de signalement

- Un **moniteur d'uptime externe** interroge `GET /health/ready` toutes les
  60 secondes. Deux échecs consécutifs déclenchent une alerte.
- **Canal de signalement** : notification par e-mail (et, en production réelle, un
  canal instantané type Slack/Telegram) vers le développeur responsable.
- Les alertes de **temps de réponse** et de **taux d'erreur** sont dérivées de
  l'analyse des journaux applicatifs.

## 5. Partie opérationnelle vs simulée

Par honnêteté vis-à-vis du jury (le référentiel autorise la mise en situation
fictive) :

- **Réel dans le code** : les deux sondes `/health` et `/health/ready` (avec ping
  PostgreSQL et bascule en `503`), la journalisation structurée des requêtes.
- **Décrit / simulé** : le moniteur d'uptime externe, les canaux d'alerte et les
  seuils, qui supposent un déploiement en production avec des utilisateurs réels —
  hors du périmètre d'un projet de fin d'études au backend simulé, mais spécifiés
  ici pour montrer le dispositif cible.

## 6. Démonstration

```bash
# Serveur démarré (voir docs/deploiement.md)
curl -i http://localhost:8080/health
# HTTP/1.1 200 OK
# {"status":"UP"}

curl -i http://localhost:8080/health/ready
# HTTP/1.1 200 OK
# {"status":"UP","checks":{"database":"UP"}}

# Base arrêtée : la sonde de disponibilité bascule
curl -i http://localhost:8080/health/ready
# HTTP/1.1 503 Service Unavailable
# {"status":"DOWN","checks":{"database":"DOWN"}}
```
