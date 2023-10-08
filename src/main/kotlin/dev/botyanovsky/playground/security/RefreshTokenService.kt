package dev.botyanovsky.playground.security

import dev.botyanovsky.playground.security.entity.RefreshTokenEntity
import dev.botyanovsky.playground.security.repository.AccountRepository
import dev.botyanovsky.playground.security.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class RefreshTokenService(
    val refreshTokenRepository: RefreshTokenRepository,
    val accountRepository: AccountRepository,
    @Value("\${app.security.refresh-token-duration}") val refreshTokenDuration: Duration
) {

    private val random: SecureRandom = SecureRandom()
    private val encoder = Base64.getUrlEncoder().withoutPadding()

    companion object {
        const val tokenSize = 64
    }

    fun findByToken(token: String): RefreshTokenEntity? {
        return refreshTokenRepository.findByToken(token)
    }

    fun createRefreshToken(userId: Long): RefreshTokenEntity {
        var refreshToken = RefreshTokenEntity()

        refreshToken.account = accountRepository.findById(userId).orElseThrow()
        refreshToken.expiryDate = Instant.now().plusMillis(refreshTokenDuration.toMillis())
        refreshToken.token = generateRefreshToken()
        refreshToken = refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    private fun generateRefreshToken(): String {
        val buffer = ByteArray(tokenSize)
        random.nextBytes(buffer)
        return encoder.encodeToString(buffer)
    }

    fun verifyExpiration(token: RefreshTokenEntity): RefreshTokenEntity {
        if (token.expiryDate?.compareTo(Instant.now())!! < 0) {
            refreshTokenRepository.delete(token)
//            throw TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request")
        }
        return token
    }

    fun delete(token: RefreshTokenEntity) {
        refreshTokenRepository.delete(token)
    }

    @Transactional
    fun deleteByUserId(accountId: Long) {
//        refreshTokenRepository.deleteByAccountId(userId)
    }


}