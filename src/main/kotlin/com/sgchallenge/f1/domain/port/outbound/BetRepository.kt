package com.sgchallenge.f1.domain.port.outbound

import com.sgchallenge.f1.domain.model.Bet
import java.util.UUID

interface BetRepository {
    fun save(bet: Bet): Bet
    fun findAll(): List<Bet>
    fun findByUserId(userId: UUID): List<Bet>
    fun findByEventId(eventId: Long): List<Bet>
}
