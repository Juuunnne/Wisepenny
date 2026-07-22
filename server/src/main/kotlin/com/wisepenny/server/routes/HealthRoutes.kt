package com.wisepenny.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String)

/**
 * Liveness probe. Kept dependency-free in the skeleton; a later timebox enriches
 * it with a real DB ping (and 503 when the database is down) for supervision.
 */
fun Application.healthRoutes() {
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, HealthResponse(status = "UP"))
        }
    }
}
