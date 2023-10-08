package dev.botyanovsky.playground.domain;

import jakarta.persistence.*

@Entity
@Table(name = "someberry")
open class SomeEntity : BaseEntity<Long>(null) {
    // todo all open for entity props, plugin for jpa?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "name", nullable = false)
    open var name: String? = null

}
