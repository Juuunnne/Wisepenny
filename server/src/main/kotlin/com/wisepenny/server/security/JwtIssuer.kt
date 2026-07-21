package com.wisepenny.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.wisepenny.server.plugins.JwtSettings
import java.util.Date
import java.util.UUID

/** Issues signed access tokens whose subject is the user id and audience/issuer match the verifier. */
class JwtIssuer(private val settings: JwtSettings) {

    /** Returns the signed token and its lifetime in seconds. */
    fun issue(userId: UUID, email: String): Pair<String, Long> {
        val now = System.currentTimeMillis()
        val token = JWT.create()
            .withIssuer(settings.issuer)
            .withAudience(settings.audience)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + settings.expiresInSeconds * 1000))
            .sign(Algorithm.HMAC256(settings.secret))
        return token to settings.expiresInSeconds
    }
}
