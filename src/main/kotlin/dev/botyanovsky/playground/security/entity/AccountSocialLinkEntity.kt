package dev.botyanovsky.playground.security.entity

import dev.botyanovsky.playground.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "account_social_link")
open class AccountSocialLinkEntity : BaseEntity<Long>(null) {

    companion object {
        val socialTypeGithub = "github"
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    open var account: AccountEntity? = null

    @Column(name = "social_type")
    open var socialType: String? = null

    @Column(name = "social_id")
    open var socialId: String? = null

}