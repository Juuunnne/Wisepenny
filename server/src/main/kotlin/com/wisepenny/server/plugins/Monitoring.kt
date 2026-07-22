package com.wisepenny.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import org.slf4j.event.Level

/** Structured request logging — one line per call, for traceability in the logs. */
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}
