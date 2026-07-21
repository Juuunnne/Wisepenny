package com.wisepenny.server

import com.wisepenny.server.plugins.configureMonitoring
import com.wisepenny.server.plugins.configureSerialization
import com.wisepenny.server.plugins.configureStatusPages
import com.wisepenny.server.routes.healthRoutes
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

/**
 * Entry point. Delegates to Ktor's [EngineMain], which reads `application.conf`
 * for the deployment port and the module list. Keeping port/secrets in config
 * (overridable by environment variables) instead of code is what lets the same
 * artifact run identically in dev, CI, and a container.
 */
fun main(args: Array<String>) = EngineMain.main(args)

/**
 * Application module referenced from `application.conf`. The skeleton wires only
 * cross-cutting concerns: JSON negotiation, request logging, a uniform error
 * envelope, and a liveness probe. Business endpoints arrive in later timeboxes.
 */
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureStatusPages()
    healthRoutes()
}
