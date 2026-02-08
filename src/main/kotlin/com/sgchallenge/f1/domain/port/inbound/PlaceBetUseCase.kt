package com.sgchallenge.f1.domain.port.inbound

import com.sgchallenge.f1.domain.model.Bet
import java.math.BigDecimal
import java.util.UUID

interface PlaceBetUseCase {
    fun placeBet(userId: UUID, eventId: Long, driverId: Int, amount: BigDecimal, odds: Int): Bet
}
