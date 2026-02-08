package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import com.sgchallenge.f1.domain.model.Bet
import com.sgchallenge.f1.domain.port.outbound.BetRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class BetRepositoryAdapter(
    private val jpa: BetJpaRepository
) : BetRepository {

    override fun save(bet: Bet): Bet =
        jpa.save(bet.toEntity()).toDomain()

    override fun findAll(): List<Bet> =
        jpa.findAll().map { it.toDomain() }

    override fun findByUserId(userId: UUID): List<Bet> =
        jpa.findByUserId(userId).map { it.toDomain() }

    override fun findByEventId(eventId: Long): List<Bet> =
        jpa.findByEventId(eventId).map { it.toDomain() }
}

private fun BetJpaEntity.toDomain() = Bet(
    id = id,
    userId = userId,
    eventId = eventId,
    driverId = driverId,
    amount = amount,
    odds = odds,
    status = status
)

private fun Bet.toEntity() = BetJpaEntity(
    id = id,
    userId = userId,
    eventId = eventId,
    driverId = driverId,
    amount = amount,
    odds = odds,
    status = status
)
