package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "event_outcomes")
class EventOutcomeJpaEntity(
    @Id val eventId: Long = 0,
    val winnerDriverId: Int = 0
)
