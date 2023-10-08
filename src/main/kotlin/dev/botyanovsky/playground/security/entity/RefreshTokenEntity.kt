package dev.botyanovsky.playground.security.entity

import dev.botyanovsky.playground.domain.BaseEntity
import jakarta.persistence.*
import java.time.Instant

@Entity(name = "refresh_token")
open class RefreshTokenEntity : BaseEntity<Long>(null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var account: AccountEntity? = null

    // todo need to store access token it was given for?
    // but now not expiring access token after refresh, it available for all the date it's given
    // so one can generate unlimited amount of access tokens simultaneously

    open var token: String? = null

    open var expiryDate: Instant? = null

    fun isExpired(): Boolean {
        return expiryDate!!.isBefore(Instant.now())
    }

}