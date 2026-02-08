package com.sgchallenge.f1.domain.port.inbound

import com.sgchallenge.f1.domain.model.EventWithMarket

interface ListEventsUseCase {
    fun listEvents(sessionType: String?, year: Int?, country: String?): List<EventWithMarket>
}
