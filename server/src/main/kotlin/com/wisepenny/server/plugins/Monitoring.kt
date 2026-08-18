package com.wisepenny.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.calllogging.processingTimeMillis
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level

/**
 * Journalisation structurée des requêtes HTTP (greffon CallLogging de Ktor).
 *
 * Une ligne par appel, contenant **méthode, chemin, code de statut et durée** de
 * traitement. Ces lignes alimentent les indicateurs de performance décrits dans
 * `docs/supervision.md` (temps de réponse, taux d'erreurs 5xx, échecs d'auth).
 *
 * Minimisation (donnée financière) : seuls la méthode, le chemin (sans paramètre de
 * requête, susceptible de porter un montant ou un identifiant), le statut et la
 * durée sont journalisés — jamais de corps de requête ni d'en-tête `Authorization`.
 * La sortie et la rotation des journaux (30 jours) sont configurées dans
 * `logback.xml`.
 */
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val method = call.request.httpMethod.value
            val path = call.request.path()
            val status = call.response.status()?.value ?: "-"
            val durationMs = call.processingTimeMillis()
            "$method $path -> $status (${durationMs}ms)"
        }
    }
}
