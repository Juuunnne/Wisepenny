package com.wisepenny.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

/** Name of the JWT auth provider; referenced by `authenticate(JWT_AUTH) { ... }`. */
const val JWT_AUTH = "auth-jwt"

/** JWT settings read once from config, shared by the verifier and the token issuer. */
data class JwtSettings(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expiresInSeconds: Long,
)

fun Application.jwtSettings(): JwtSettings {
    val c = environment.config
    return JwtSettings(
        secret = c.property("jwt.secret").getString(),
        issuer = c.property("jwt.issuer").getString(),
        audience = c.property("jwt.audience").getString(),
        realm = c.property("jwt.realm").getString(),
        expiresInSeconds = c.property("jwt.expiresInSeconds").getString().toLong(),
    )
}

/**
 * Installs Bearer-JWT authentication. A token is accepted only if the HMAC signature,
 * issuer and audience all match, and it carries a subject (our user id). Ktor answers
 * 401 automatically when a protected route is hit without a valid token — that is the
 * baseline against OWASP A07 (Identification & Authentication Failures).
 */
fun Application.configureSecurity(settings: JwtSettings) {
    install(Authentication) {
        jwt(JWT_AUTH) {
            realm = settings.realm
            verifier(
                JWT.require(Algorithm.HMAC256(settings.secret))
                    .withIssuer(settings.issuer)
                    .withAudience(settings.audience)
                    .build(),
            )
            validate { credential ->
                if (credential.payload.subject != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
