package com.sgchallenge.f1.domain.model

import com.sgchallenge.f1.domain.exception.InsufficientBalanceException
import com.sgchallenge.f1.domain.exception.InvalidAmountException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

class UserTest {

    private val userId = UUID.randomUUID()

    @Test
    fun `debit reduces balance`() {
        val user = User(userId, BigDecimal("100.00"))
        val result = user.debit(BigDecimal("30.00"))
        assertEquals(BigDecimal("70.00"), result.balance)
    }

    @Test
    fun `debit with exact balance leaves zero`() {
        val user = User(userId, BigDecimal("50.00"))
        val result = user.debit(BigDecimal("50.00"))
        assertEquals(BigDecimal("0.00"), result.balance)
    }

    @Test
    fun `debit with insufficient balance throws`() {
        val user = User(userId, BigDecimal("10.00"))
        assertThrows<InsufficientBalanceException> {
            user.debit(BigDecimal("20.00"))
        }
    }

    @Test
    fun `debit with zero amount throws`() {
        val user = User(userId, BigDecimal("100.00"))
        assertThrows<InvalidAmountException> {
            user.debit(BigDecimal.ZERO)
        }
    }

    @Test
    fun `debit with negative amount throws`() {
        val user = User(userId, BigDecimal("100.00"))
        assertThrows<InvalidAmountException> {
            user.debit(BigDecimal("-5.00"))
        }
    }

    @Test
    fun `credit increases balance`() {
        val user = User(userId, BigDecimal("100.00"))
        val result = user.credit(BigDecimal("50.00"))
        assertEquals(BigDecimal("150.00"), result.balance)
    }

    @Test
    fun `credit with zero amount throws`() {
        val user = User(userId, BigDecimal("100.00"))
        assertThrows<InvalidAmountException> {
            user.credit(BigDecimal.ZERO)
        }
    }

    @Test
    fun `credit with negative amount throws`() {
        val user = User(userId, BigDecimal("100.00"))
        assertThrows<InvalidAmountException> {
            user.credit(BigDecimal("-10.00"))
        }
    }

    @Test
    fun `debit does not mutate original`() {
        val user = User(userId, BigDecimal("100.00"))
        user.debit(BigDecimal("30.00"))
        assertEquals(BigDecimal("100.00"), user.balance)
    }
}
