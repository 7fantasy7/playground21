package dev.botyanovsky.playground.security

import dev.botyanovsky.playground.security.details.UserDetailsImpl
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors

@Component
class TokenGenerator(
    val jwtEncoder: JwtEncoder
) {

    companion object {
        const val tokenExpiryHours = 4L
        const val issuer = "botyanovsky.dev"
    }

    fun generate(authentication: Authentication): String {
        val user = authentication.principal as UserDetailsImpl
        val now: Instant = Instant.now()
        val expiry = Duration.ofHours(tokenExpiryHours)
        val scope: String = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))

        val claims: JwtClaimsSet = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(now.plus(expiry))
            .subject(user.id.toString())
            .claim("username", user.username)
            .claim("roles", scope)
            .claim("email", user.email.orEmpty())
            .build()

        val token: String = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue

        return token
    }

}