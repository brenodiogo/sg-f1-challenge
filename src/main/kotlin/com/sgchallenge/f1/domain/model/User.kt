package com.sgchallenge.f1.domain.model

import com.sgchallenge.f1.domain.exception.InsufficientBalanceException
import com.sgchallenge.f1.domain.exception.InvalidAmountException
import java.math.BigDecimal
import java.util.UUID

data class User(val id: UUID, val balance: BigDecimal) {

    fun debit(amount: BigDecimal): User {
        requirePositive(amount)
        if (amount > balance) throw InsufficientBalanceException(balance, amount)
        return copy(balance = balance - amount)
    }

    fun credit(amount: BigDecimal): User {
        requirePositive(amount)
        return copy(balance = balance + amount)
    }

    private fun requirePositive(amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) throw InvalidAmountException(amount)
    }
}
