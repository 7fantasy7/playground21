package dev.botyanovsky.playground.repository

import dev.botyanovsky.playground.domain.SomeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SomeRepository : JpaRepository<SomeEntity, Long> {
}