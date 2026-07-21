package com.wisepenny.server.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Builds the connection pool, connects Exposed, and runs the startup migration.
 * Connection details come from `application.conf` (overridable by env vars), so the
 * same binary runs unchanged against a local Docker Postgres, CI, or a container.
 */
object DatabaseFactory {

    fun init(app: Application): Database {
        val config = app.environment.config

        val hikari = HikariConfig().apply {
            jdbcUrl = config.property("db.jdbcUrl").getString()
            username = config.property("db.user").getString()
            password = config.property("db.password").getString()
            driverClassName = config.property("db.driver").getString()
            maximumPoolSize = 5
            poolName = "wisepenny-pool"
        }

        val database = Database.connect(HikariDataSource(hikari))

        // Idempotent migration: creates tables/columns that don't exist yet, leaves the
        // rest untouched. Enough for a demo; a real product would use versioned migrations.
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(*allTables)
        }

        return database
    }
}
