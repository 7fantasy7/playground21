package dev.botyanovsky.playground.security.entity

import dev.botyanovsky.playground.domain.BaseEntity
import jakarta.persistence.*

// todo tables, liquibase/flyway?
@Entity
@Table(name = "account")
open class AccountEntity : BaseEntity<Long>(null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Long? = null

    @Column(name = "login")
    open var username: String? = null

    @Column(name = "email")
    open var email: String? = null

    @Column(name = "passwordHash")
    open var passwordHash: String? = null

}