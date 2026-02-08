package com.sgchallenge.f1.domain.model

import com.sgchallenge.f1.domain.exception.BetAlreadySettledException
import com.sgchallenge.f1.domain.exception.InvalidOddsException
import java.math.BigDecimal
import java.util.UUID

data class Bet(
    val id: UUID,
    val userId: UUID,
    val eventId: Long,
    val driverId: Int,
    val amount: BigDecimal,
    val odds: Int,
    val status: BetStatus = BetStatus.PENDING
) {
    init {
        if (odds !in VALID_ODDS) throw InvalidOddsException(odds)
    }

    fun settle(winnerDriverId: Int): Bet {
        if (status != BetStatus.PENDING) throw BetAlreadySettledException(id)
        val newStatus = if (driverId == winnerDriverId) BetStatus.WON else BetStatus.LOST
        return copy(status = newStatus)
    }

    fun prize(): BigDecimal = when (status) {
        BetStatus.WON -> amount * odds.toBigDecimal()
        else -> BigDecimal.ZERO
    }

    companion object {
        private val VALID_ODDS = setOf(2, 3, 4)
    }
}
