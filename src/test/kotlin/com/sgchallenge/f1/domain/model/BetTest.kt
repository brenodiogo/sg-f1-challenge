package com.sgchallenge.f1.domain.model

import com.sgchallenge.f1.domain.exception.BetAlreadySettledException
import com.sgchallenge.f1.domain.exception.InvalidOddsException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

class BetTest {

    private val betId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val eventId = 9001L
    private val driverId = 44
    private val winnerDriverId = 44
    private val loserDriverId = 77

    private fun pendingBet(odds: Int = 3) = Bet(
        id = betId,
        userId = userId,
        eventId = eventId,
        driverId = driverId,
        amount = BigDecimal("25.00"),
        odds = odds
    )

    @Test
    fun `settle with matching driver results in WON`() {
        val settled = pendingBet().settle(winnerDriverId)
        assertEquals(BetStatus.WON, settled.status)
    }

    @Test
    fun `settle with non-matching driver results in LOST`() {
        val settled = pendingBet().settle(loserDriverId)
        assertEquals(BetStatus.LOST, settled.status)
    }

    @Test
    fun `settle on already won bet throws`() {
        val won = pendingBet().settle(winnerDriverId)
        assertThrows<BetAlreadySettledException> {
            won.settle(winnerDriverId)
        }
    }

    @Test
    fun `settle on already lost bet throws`() {
        val lost = pendingBet().settle(loserDriverId)
        assertThrows<BetAlreadySettledException> {
            lost.settle(loserDriverId)
        }
    }

    @Test
    fun `prize for won bet is amount times odds`() {
        val won = pendingBet(odds = 4).settle(winnerDriverId)
        assertEquals(BigDecimal("100.00"), won.prize())
    }

    @Test
    fun `prize for lost bet is zero`() {
        val lost = pendingBet().settle(loserDriverId)
        assertEquals(BigDecimal.ZERO, lost.prize())
    }

    @Test
    fun `prize for pending bet is zero`() {
        assertEquals(BigDecimal.ZERO, pendingBet().prize())
    }

    @Test
    fun `invalid odds throws on construction`() {
        assertThrows<InvalidOddsException> {
            pendingBet(odds = 5)
        }
    }

    @Test
    fun `odds of 1 is invalid`() {
        assertThrows<InvalidOddsException> {
            pendingBet(odds = 1)
        }
    }

    @Test
    fun `valid odds 2, 3, 4 do not throw`() {
        listOf(2, 3, 4).forEach { odds ->
            pendingBet(odds = odds)
        }
    }

    @Test
    fun `settle does not mutate original`() {
        val bet = pendingBet()
        bet.settle(winnerDriverId)
        assertEquals(BetStatus.PENDING, bet.status)
    }
}
