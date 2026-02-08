package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BetJpaRepository : JpaRepository<BetJpaEntity, UUID> {
    fun findByEventId(eventId: Long): List<BetJpaEntity>
    fun findByUserId(userId: UUID): List<BetJpaEntity>
}
