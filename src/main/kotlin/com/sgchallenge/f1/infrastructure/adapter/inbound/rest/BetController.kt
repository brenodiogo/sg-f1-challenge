package com.sgchallenge.f1.infrastructure.adapter.inbound.rest

import com.sgchallenge.f1.domain.model.BetStatus
import com.sgchallenge.f1.domain.port.inbound.PlaceBetUseCase
import com.sgchallenge.f1.domain.port.outbound.BetRepository
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/bets")
class BetController(
    private val placeBetUseCase: PlaceBetUseCase,
    private val betRepository: BetRepository
) {

    @GetMapping
    fun listBets(
        @RequestParam(required = false) userId: UUID?,
        @RequestParam(required = false) eventId: Long?,
        @RequestParam(required = false) status: BetStatus?
    ): List<BetResponse> {
        val bets = when {
            userId != null -> betRepository.findByUserId(userId)
            eventId != null -> betRepository.findByEventId(eventId)
            else -> betRepository.findAll()
        }
        return bets
            .filter { status == null || it.status == status }
            .map { BetResponse(it.id, it.userId, it.eventId, it.driverId, it.amount, it.odds, it.status.name) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun placeBet(@RequestBody request: PlaceBetRequest): BetResponse {
        val bet = placeBetUseCase.placeBet(
            userId = request.userId,
            eventId = request.eventId,
            driverId = request.driverId,
            amount = request.amount,
            odds = request.odds
        )
        return BetResponse(
            id = bet.id,
            userId = bet.userId,
            eventId = bet.eventId,
            driverId = bet.driverId,
            amount = bet.amount,
            odds = bet.odds,
            status = bet.status.name
        )
    }

    data class PlaceBetRequest(
        @Schema(example = "00000000-0000-0000-0000-000000000001") val userId: UUID,
        @Schema(example = "9222") val eventId: Long,
        @Schema(example = "1") val driverId: Int,
        @Schema(example = "20") val amount: BigDecimal,
        @Schema(example = "2") val odds: Int
    )

    data class BetResponse(
        val id: UUID,
        val userId: UUID,
        val eventId: Long,
        val driverId: Int,
        val amount: BigDecimal,
        val odds: Int,
        val status: String
    )
}
