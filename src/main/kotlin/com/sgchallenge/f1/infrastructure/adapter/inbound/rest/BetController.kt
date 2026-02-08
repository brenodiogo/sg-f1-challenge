package com.sgchallenge.f1.infrastructure.adapter.inbound.rest

import com.sgchallenge.f1.domain.port.inbound.PlaceBetUseCase
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/bets")
class BetController(
    private val placeBetUseCase: PlaceBetUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun placeBet(@RequestBody request: PlaceBetRequest): PlaceBetResponse {
        val bet = placeBetUseCase.placeBet(
            userId = request.userId,
            eventId = request.eventId,
            driverId = request.driverId,
            amount = request.amount,
            odds = request.odds
        )
        return PlaceBetResponse(
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

    data class PlaceBetResponse(
        val id: UUID,
        val userId: UUID,
        val eventId: Long,
        val driverId: Int,
        val amount: BigDecimal,
        val odds: Int,
        val status: String
    )
}
