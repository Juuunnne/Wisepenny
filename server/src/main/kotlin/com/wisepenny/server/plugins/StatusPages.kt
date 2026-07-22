package com.wisepenny.server.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

/** Uniform error envelope, matching API_SPEC.md: `{code, message}`. */
@Serializable
data class ApiError(val code: String, val message: String)

/** 422 — request is well-formed but fails a business/validation rule. */
class ValidationException(message: String) : RuntimeException(message)

/**
 * 404 — resource does not exist, OR the caller is not allowed to know it exists.
 * Returning 404 (not 403) for ownership violations avoids leaking which ids are
 * real (OWASP A01 — Broken Access Control).
 */
class NotFoundException(message: String) : RuntimeException(message)

/** 401 — credentials are missing or invalid. */
class UnauthorizedException(message: String) : RuntimeException(message)

/** Maps exceptions to the shared [ApiError] envelope so every failure looks the same. */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ApiError("UNAUTHORIZED", cause.message ?: "Unauthorized"),
            )
        }
        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiError("BAD_REQUEST", cause.message ?: "Malformed request"),
            )
        }
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.UnprocessableEntity,
                ApiError("VALIDATION", cause.message ?: "Invalid request"),
            )
        }
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ApiError("NOT_FOUND", cause.message ?: "Resource not found"),
            )
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiError("INTERNAL", "Unexpected server error"),
            )
        }
    }
}
