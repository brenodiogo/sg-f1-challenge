package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import com.sgchallenge.f1.domain.model.EventOutcome
import com.sgchallenge.f1.domain.port.outbound.EventOutcomeRepository
import org.springframework.stereotype.Component

@Component
class EventOutcomeRepositoryAdapter(
    private val jpa: EventOutcomeJpaRepository
) : EventOutcomeRepository {

    override fun save(outcome: EventOutcome): EventOutcome =
        jpa.save(outcome.toEntity()).toDomain()

    override fun existsByEventId(eventId: Long): Boolean =
        jpa.existsByEventId(eventId)
}

private fun EventOutcomeJpaEntity.toDomain() = EventOutcome(
    eventId = eventId,
    winnerDriverId = winnerDriverId
)

private fun EventOutcome.toEntity() = EventOutcomeJpaEntity(
    eventId = eventId,
    winnerDriverId = winnerDriverId
)
