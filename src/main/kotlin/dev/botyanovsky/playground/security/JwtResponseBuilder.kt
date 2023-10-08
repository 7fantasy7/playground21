package dev.botyanovsky.playground.security

import dev.botyanovsky.playground.security.details.UserDetailsImpl
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JwtResponseBuilder(
    val tokenGenerator: TokenGenerator,
    val refreshTokenService: RefreshTokenService
) {

    fun build(authentication: Authentication): JwtResponse {
        val token = tokenGenerator.generate(authentication)

        val principal = authentication.principal as UserDetailsImpl

        val refreshToken = refreshTokenService.createRefreshToken(principal.id)

        return JwtResponse(
            token, refreshToken.token!!, principal.id,
            principal.username, principal.email
        )
    }
}