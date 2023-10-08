package dev.botyanovsky.playground.security.repository

import dev.botyanovsky.playground.security.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {

    fun findByToken(token: String): RefreshTokenEntity?

    @Modifying
    fun deleteByAccountId(accountId: Long)
}