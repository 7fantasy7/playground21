package dev.botyanovsky.playground.security

//import io.swagger.v3.oas.annotations.Operation
import dev.botyanovsky.playground.security.details.UserDetailsImpl
import dev.botyanovsky.playground.security.repository.AccountRepository
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthApi(
    val authenticationManager: AuthenticationManager,
    val refreshTokenService: RefreshTokenService,
    val jwtResponseBuilder: JwtResponseBuilder,
    val accountRepository: AccountRepository,
    @Qualifier("loginRateLimiter") val loginRateLimiter: RateLimiterRegistry,
    @Qualifier("refreshRateLimiter") val refreshRateLimiter: RateLimiterRegistry
) {

    data class AuthRequest(
        @field:NotBlank val username: String,
        @field:NotBlank val password: String
    )

    //    @Operation(hidden = true)
    @PostMapping("/login")
    fun login(
//        @RequestHeader(value = "User-Agent", required = false) userAgent: String,
        @Valid @RequestBody request: AuthRequest
    ): ResponseEntity<JwtResponse> {
        if (!loginRateLimiter.rateLimiter(request.username).acquirePermission()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()
        }

        return try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            SecurityContextHolder.getContext().authentication = authentication

            return ResponseEntity.ok(jwtResponseBuilder.build(authentication))
        } catch (ex: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    data class RefreshTokenRequest(@field:NotBlank val refreshToken: String)

    //        @Operation(hidden = true)
    @PostMapping("/token/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<JwtResponse> {
        val refreshToken = refreshTokenService.findByToken(request.refreshToken)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        if (!refreshRateLimiter.rateLimiter(refreshToken.account?.id.toString()).acquirePermission()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()
        }

        if (refreshToken.isExpired()) {
            refreshTokenService.delete(refreshToken)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val principal = refreshToken.account?.let { UserDetailsImpl.build(it) }
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val authentication = UsernamePasswordAuthenticationToken(
            principal, null, mutableListOf()
        )

        return ResponseEntity.ok(jwtResponseBuilder.build(authentication))
    }

    data class Account(val id: Long, val username: String, val email: String)

    @GetMapping("/me")
    fun me(authentication: Authentication): ResponseEntity<Account> {
        val username = (authentication.principal as Jwt).getClaim<String>("username")
        val accountEntity = accountRepository.findByUsername(username)

        val account = Account(
            id = accountEntity?.id!!,
            username = accountEntity.username!!,
            email = accountEntity.email!!
        )

        return ResponseEntity.ok(account)
    }
}