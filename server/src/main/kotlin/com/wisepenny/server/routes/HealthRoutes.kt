package com.wisepenny.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Serializable
data class HealthResponse(val status: String)

@Serializable
data class ReadinessResponse(
    val status: String,
    val checks: Map<String, String>,
)

/**
 * Sondes de supervision exposées par le serveur.
 *
 * - `GET /health`       — sonde de vivacité (*liveness*). Répond 200 tant que le
 *   processus tourne. Une réponse absente ou différente signale à l'orchestrateur
 *   (ou à la supervision externe) qu'il faut redémarrer l'instance.
 * - `GET /health/ready` — sonde de disponibilité (*readiness*). Vérifie que la base
 *   PostgreSQL répond via une requête `SELECT 1`. Renvoie **503** si la base est
 *   injoignable, pour sortir l'instance du trafic sans la tuer.
 *
 * Ces deux sondes constituent le point d'ancrage de la supervision décrite dans
 * `docs/supervision.md` (seuils d'alerte, signalement).
 */
fun Application.healthRoutes(database: Database) {
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, HealthResponse(status = "UP"))
        }
        get("/health/ready") {
            val databaseUp = runCatching {
                transaction(database) { exec("SELECT 1") }
            }.isSuccess

            call.respond(
                if (databaseUp) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable,
                ReadinessResponse(
                    status = if (databaseUp) "UP" else "DOWN",
                    checks = mapOf("database" to if (databaseUp) "UP" else "DOWN"),
                ),
            )
        }
    }
}
