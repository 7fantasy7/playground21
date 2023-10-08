package dev.botyanovsky.playground.security.repository

import dev.botyanovsky.playground.security.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<AccountEntity, Long> {
    fun findByUsername(username: String): AccountEntity?
}