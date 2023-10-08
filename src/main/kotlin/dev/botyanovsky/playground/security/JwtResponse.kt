package dev.botyanovsky.playground.security

data class JwtResponse(
    val token: String,
    val refreshToken: String,
    val id: Long,
    val username: String,
    val email: String?,
    var type: String = "Bearer"
)
