package com.sgchallenge.f1.infrastructure.adapter.inbound.rest

import com.sgchallenge.f1.domain.model.EventWithMarket
import com.sgchallenge.f1.domain.model.PagedResult
import com.sgchallenge.f1.domain.port.inbound.ListEventsUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/events")
class EventController(
    private val listEventsUseCase: ListEventsUseCase
) {

    @GetMapping
    fun listEvents(
        @RequestParam(required = false) sessionType: String?,
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) country: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "3") size: Int
    ): PagedResult<EventWithMarket> = listEventsUseCase.listEvents(sessionType, year, country, page, size)
}
