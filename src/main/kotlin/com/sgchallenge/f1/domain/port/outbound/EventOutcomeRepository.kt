package com.sgchallenge.f1.domain.port.outbound

import com.sgchallenge.f1.domain.model.EventOutcome

interface EventOutcomeRepository {
    fun save(outcome: EventOutcome): EventOutcome
    fun findByEventId(eventId: Long): EventOutcome?
    fun existsByEventId(eventId: Long): Boolean
}
