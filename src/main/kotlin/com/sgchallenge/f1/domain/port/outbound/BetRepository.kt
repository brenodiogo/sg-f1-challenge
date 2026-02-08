package com.sgchallenge.f1.domain.port.outbound

import com.sgchallenge.f1.domain.model.Bet

interface BetRepository {
    fun save(bet: Bet): Bet
    fun findByEventId(eventId: Long): List<Bet>
}
