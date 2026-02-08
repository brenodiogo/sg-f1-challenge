package com.sgchallenge.f1.domain.exception

sealed class DomainException(message: String) : RuntimeException(message)

class InsufficientBalanceException(balance: java.math.BigDecimal, amount: java.math.BigDecimal) :
    DomainException("Insufficient balance: $balance, required: $amount")

class BetAlreadySettledException(betId: java.util.UUID) :
    DomainException("Bet $betId is already settled")

class EventAlreadySettledException(eventId: Long) :
    DomainException("Event $eventId is already settled")

class InvalidAmountException(amount: java.math.BigDecimal) :
    DomainException("Amount must be positive: $amount")

class InvalidOddsException(odds: Int) :
    DomainException("Odds must be 2, 3 or 4: $odds")

class UserNotFoundException(userId: java.util.UUID) :
    DomainException("User not found: $userId")
