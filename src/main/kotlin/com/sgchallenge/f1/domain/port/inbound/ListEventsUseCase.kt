package com.sgchallenge.f1.domain.port.inbound

import com.sgchallenge.f1.domain.model.EventWithMarket
import com.sgchallenge.f1.domain.model.PagedResult

interface ListEventsUseCase {
    fun listEvents(sessionType: String?, year: Int?, country: String?, page: Int, size: Int): PagedResult<EventWithMarket>
}
