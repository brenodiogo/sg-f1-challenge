package com.sgchallenge.f1.application.service

import com.sgchallenge.f1.domain.model.DriverOdds
import com.sgchallenge.f1.domain.model.EventWithMarket
import com.sgchallenge.f1.domain.port.inbound.ListEventsUseCase
import com.sgchallenge.f1.domain.port.outbound.F1EventProvider
import org.springframework.stereotype.Service

@Service
class ListEventsService(
    private val f1EventProvider: F1EventProvider
) : ListEventsUseCase {

    override fun listEvents(sessionType: String?, year: Int?, country: String?): List<EventWithMarket> {
        val sessions = f1EventProvider.getSessions(sessionType, year, country)
        return sessions.map { session ->
            val drivers = f1EventProvider.getDrivers(session.sessionKey)
            val market = drivers.map { driver ->
                DriverOdds(driver, VALID_ODDS.random())
            }
            EventWithMarket(session, market)
        }
    }

    companion object {
        private val VALID_ODDS = listOf(2, 3, 4)
    }
}
