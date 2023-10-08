package dev.botyanovsky.playground.domain

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable

abstract class BaseEntity<T>(
    @Id private val id: T?
) : Persistable<T> {

    override fun getId(): T? = id

    override fun isNew() = id == null

    override fun hashCode() = javaClass.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false

        return getId() != null && getId() == (other as BaseEntity<*>).getId()
    }

    override fun toString() = "Entity[type=${this.javaClass.name},id=${getId()}]"

}