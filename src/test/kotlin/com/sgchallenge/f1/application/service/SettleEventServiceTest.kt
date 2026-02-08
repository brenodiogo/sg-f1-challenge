package com.sgchallenge.f1.application.service

import com.sgchallenge.f1.domain.exception.EventAlreadySettledException
import com.sgchallenge.f1.domain.model.Bet
import com.sgchallenge.f1.domain.model.BetStatus
import com.sgchallenge.f1.domain.model.User
import com.sgchallenge.f1.domain.port.outbound.BetRepository
import com.sgchallenge.f1.domain.port.outbound.EventOutcomeRepository
import com.sgchallenge.f1.domain.port.outbound.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SettleEventServiceTest {

    @Mock
    lateinit var eventOutcomeRepository: EventOutcomeRepository

    @Mock
    lateinit var betRepository: BetRepository

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var service: SettleEventService

    private val eventId = 9001L
    private val winnerDriverId = 44
    private val loserDriverId = 77

    private val winnerUserId = UUID.randomUUID()
    private val loserUserId = UUID.randomUUID()

    private fun bet(userId: UUID, driverId: Int, amount: BigDecimal = BigDecimal("25.00"), odds: Int = 3) = Bet(
        id = UUID.randomUUID(),
        userId = userId,
        eventId = eventId,
        driverId = driverId,
        amount = amount,
        odds = odds
    )

    @Test
    fun `settles winning and losing bets correctly`() {
        whenever(eventOutcomeRepository.existsByEventId(eventId)).thenReturn(false)
        whenever(eventOutcomeRepository.save(any())).thenAnswer { it.arguments[0] }

        val winningBet = bet(winnerUserId, winnerDriverId)
        val losingBet = bet(loserUserId, loserDriverId)
        whenever(betRepository.findByEventId(eventId)).thenReturn(listOf(winningBet, losingBet))
        whenever(betRepository.save(any<Bet>())).thenAnswer { it.arguments[0] }

        val winnerUser = User(winnerUserId, BigDecimal("75.00"))
        whenever(userRepository.findById(winnerUserId)).thenReturn(winnerUser)
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }

        service.settleEvent(eventId, winnerDriverId)

        val betCaptor = argumentCaptor<Bet>()
        verify(betRepository, times(2)).save(betCaptor.capture())
        val savedBets = betCaptor.allValues
        assertEquals(BetStatus.WON, savedBets[0].status)
        assertEquals(BetStatus.LOST, savedBets[1].status)
    }

    @Test
    fun `credits winner with prize amount`() {
        whenever(eventOutcomeRepository.existsByEventId(eventId)).thenReturn(false)
        whenever(eventOutcomeRepository.save(any())).thenAnswer { it.arguments[0] }

        val winningBet = bet(winnerUserId, winnerDriverId, BigDecimal("25.00"), 4)
        whenever(betRepository.findByEventId(eventId)).thenReturn(listOf(winningBet))
        whenever(betRepository.save(any<Bet>())).thenAnswer { it.arguments[0] }

        val winnerUser = User(winnerUserId, BigDecimal("75.00"))
        whenever(userRepository.findById(winnerUserId)).thenReturn(winnerUser)
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }

        service.settleEvent(eventId, winnerDriverId)

        val userCaptor = argumentCaptor<User>()
        verify(userRepository).save(userCaptor.capture())
        assertEquals(BigDecimal("175.00"), userCaptor.firstValue.balance)
    }

    @Test
    fun `already settled event throws EventAlreadySettledException`() {
        whenever(eventOutcomeRepository.existsByEventId(eventId)).thenReturn(true)

        assertThrows<EventAlreadySettledException> {
            service.settleEvent(eventId, winnerDriverId)
        }
    }

    @Test
    fun `no bets for event still saves outcome`() {
        whenever(eventOutcomeRepository.existsByEventId(eventId)).thenReturn(false)
        whenever(eventOutcomeRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(betRepository.findByEventId(eventId)).thenReturn(emptyList())

        service.settleEvent(eventId, winnerDriverId)

        verify(eventOutcomeRepository).save(any())
        verify(userRepository, never()).save(any())
    }
}
