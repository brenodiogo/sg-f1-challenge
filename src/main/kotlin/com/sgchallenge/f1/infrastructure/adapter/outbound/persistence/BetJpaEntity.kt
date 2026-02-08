package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import com.sgchallenge.f1.domain.model.BetStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "bets")
class BetJpaEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val userId: UUID = UUID.randomUUID(),
    val eventId: Long = 0,
    val driverId: Int = 0,
    val amount: BigDecimal = BigDecimal.ZERO,
    val odds: Int = 2,
    @Enumerated(EnumType.STRING) var status: BetStatus = BetStatus.PENDING
)
