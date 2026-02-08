package com.sgchallenge.f1.infrastructure.adapter.inbound.rest

import com.sgchallenge.f1.domain.port.inbound.SettleEventUseCase
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/event-outcomes")
class EventOutcomeController(
    private val settleEventUseCase: SettleEventUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun settleEvent(@RequestBody request: SettleEventRequest) {
        settleEventUseCase.settleEvent(request.eventId, request.winnerDriverId)
    }

    data class SettleEventRequest(
        @Schema(example = "9222") val eventId: Long,
        @Schema(example = "1") val winnerDriverId: Int
    )
}
