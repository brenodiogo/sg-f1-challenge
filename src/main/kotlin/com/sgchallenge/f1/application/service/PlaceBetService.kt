package com.sgchallenge.f1.application.service

import com.sgchallenge.f1.domain.exception.UserNotFoundException
import com.sgchallenge.f1.domain.model.Bet
import com.sgchallenge.f1.domain.port.inbound.PlaceBetUseCase
import com.sgchallenge.f1.domain.port.outbound.BetRepository
import com.sgchallenge.f1.domain.port.outbound.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class PlaceBetService(
    private val userRepository: UserRepository,
    private val betRepository: BetRepository
) : PlaceBetUseCase {

    @Transactional
    override fun placeBet(userId: UUID, eventId: Long, driverId: Int, amount: BigDecimal, odds: Int): Bet {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException(userId)
        val debitedUser = user.debit(amount)
        userRepository.save(debitedUser)

        val bet = Bet(
            id = UUID.randomUUID(),
            userId = userId,
            eventId = eventId,
            driverId = driverId,
            amount = amount,
            odds = odds
        )
        return betRepository.save(bet)
    }
}
