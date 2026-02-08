package com.sgchallenge.f1.application.service

import com.sgchallenge.f1.domain.exception.EventAlreadySettledException
import com.sgchallenge.f1.domain.exception.UserNotFoundException
import com.sgchallenge.f1.domain.model.BetStatus
import com.sgchallenge.f1.domain.model.EventOutcome
import com.sgchallenge.f1.domain.port.inbound.SettleEventUseCase
import com.sgchallenge.f1.domain.port.outbound.BetRepository
import com.sgchallenge.f1.domain.port.outbound.EventOutcomeRepository
import com.sgchallenge.f1.domain.port.outbound.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SettleEventService(
    private val eventOutcomeRepository: EventOutcomeRepository,
    private val betRepository: BetRepository,
    private val userRepository: UserRepository
) : SettleEventUseCase {

    @Transactional
    override fun settleEvent(eventId: Long, winnerDriverId: Int) {
        if (eventOutcomeRepository.existsByEventId(eventId)) {
            throw EventAlreadySettledException(eventId)
        }

        eventOutcomeRepository.save(EventOutcome(eventId, winnerDriverId))

        val bets = betRepository.findByEventId(eventId)
        bets.forEach { bet ->
            val settledBet = bet.settle(winnerDriverId)
            betRepository.save(settledBet)

            if (settledBet.status == BetStatus.WON) {
                val user = userRepository.findById(settledBet.userId)
                    ?: throw UserNotFoundException(settledBet.userId)
                userRepository.save(user.credit(settledBet.prize()))
            }
        }
    }
}
