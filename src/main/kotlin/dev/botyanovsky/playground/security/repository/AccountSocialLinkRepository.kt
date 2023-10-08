package dev.botyanovsky.playground.security.repository

import dev.botyanovsky.playground.security.entity.AccountSocialLinkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountSocialLinkRepository : JpaRepository<AccountSocialLinkEntity, Long> {
    fun findAccountBySocialTypeAndSocialId(socialType: String, socialId: String): AccountSocialLinkEntity?
}