package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface EventOutcomeJpaRepository : JpaRepository<EventOutcomeJpaEntity, Long> {
    fun existsByEventId(eventId: Long): Boolean
}
