package com.sgchallenge.f1.application.service

import com.sgchallenge.f1.domain.exception.InsufficientBalanceException
import com.sgchallenge.f1.domain.exception.UserNotFoundException
import com.sgchallenge.f1.domain.model.Bet
import com.sgchallenge.f1.domain.model.BetStatus
import com.sgchallenge.f1.domain.model.User
import com.sgchallenge.f1.domain.port.outbound.BetRepository
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class PlaceBetServiceTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var betRepository: BetRepository

    @InjectMocks
    lateinit var service: PlaceBetService

    private val userId = UUID.randomUUID()
    private val user = User(userId, BigDecimal("100.00"))

    @Test
    fun `successful bet debits user and saves pending bet`() {
        whenever(userRepository.findById(userId)).thenReturn(user)
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(betRepository.save(any<Bet>())).thenAnswer { it.arguments[0] }

        val result = service.placeBet(userId, 9001, 44, BigDecimal("25.00"), 3)

        assertEquals(BetStatus.PENDING, result.status)
        assertEquals(BigDecimal("25.00"), result.amount)
        assertEquals(3, result.odds)
        assertEquals(44, result.driverId)

        val userCaptor = argumentCaptor<User>()
        verify(userRepository).save(userCaptor.capture())
        assertEquals(BigDecimal("75.00"), userCaptor.firstValue.balance)
    }

    @Test
    fun `user not found throws UserNotFoundException`() {
        whenever(userRepository.findById(userId)).thenReturn(null)

        assertThrows<UserNotFoundException> {
            service.placeBet(userId, 9001, 44, BigDecimal("25.00"), 3)
        }
    }

    @Test
    fun `insufficient balance throws InsufficientBalanceException`() {
        whenever(userRepository.findById(userId)).thenReturn(user)

        assertThrows<InsufficientBalanceException> {
            service.placeBet(userId, 9001, 44, BigDecimal("200.00"), 3)
        }
    }
}
