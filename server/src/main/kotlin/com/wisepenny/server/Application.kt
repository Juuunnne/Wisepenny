package com.wisepenny.server

import com.wisepenny.server.db.DatabaseFactory
import com.wisepenny.server.db.DevSeeder
import com.wisepenny.server.plugins.configureMonitoring
import com.wisepenny.server.plugins.configureSecurity
import com.wisepenny.server.plugins.configureSerialization
import com.wisepenny.server.plugins.configureStatusPages
import com.wisepenny.server.plugins.jwtSettings
import com.wisepenny.server.routes.accountRoutes
import com.wisepenny.server.routes.authRoutes
import com.wisepenny.server.routes.balanceRoutes
import com.wisepenny.server.routes.healthRoutes
import com.wisepenny.server.routes.savingsRoutes
import com.wisepenny.server.security.JwtIssuer
import com.wisepenny.server.service.AccountService
import com.wisepenny.server.service.AuthService
import com.wisepenny.server.service.BalanceService
import com.wisepenny.server.service.TransferService
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing

/**
 * Entry point. Delegates to Ktor's [EngineMain], which reads `application.conf`
 * for the deployment port and the module list. Keeping port/secrets in config
 * (overridable by environment variables) instead of code is what lets the same
 * artifact run identically in dev, CI, and a container.
 */
fun main(args: Array<String>) = EngineMain.main(args)

/**
 * Application module referenced from `application.conf`. Wires cross-cutting
 * concerns (JSON, logging, error envelope, JWT), initialises the database, and
 * mounts the business routes. Routes stay thin — all data access lives in services.
 */
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureStatusPages()

    val settings = jwtSettings()
    configureSecurity(settings)

    val database = DatabaseFactory.init(this)
    if (isDevEnvironment()) {
        DevSeeder.seed(database)
    }

    val jwtIssuer = JwtIssuer(settings)
    val authService = AuthService(database, jwtIssuer)
    val accountService = AccountService(database)
    val balanceService = BalanceService(database)
    val transferService = TransferService(database)

    healthRoutes()
    routing {
        authRoutes(authService)
        accountRoutes(accountService)
        balanceRoutes(balanceService)
        savingsRoutes(transferService)
    }
}

/** The demo seeder runs only here, never against a real/prod database. */
private fun Application.isDevEnvironment(): Boolean =
    environment.config.propertyOrNull("app.environment")?.getString() == "dev"
