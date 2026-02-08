package com.sgchallenge.f1.infrastructure.adapter.outbound.openf1

import com.sgchallenge.f1.domain.model.Driver
import com.sgchallenge.f1.domain.model.F1Event
import com.sgchallenge.f1.domain.port.outbound.F1EventProvider
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class OpenF1Adapter(
    private val openF1RestClient: RestClient
) : F1EventProvider {

    private val rateLimiter = SimpleRateLimiter(minIntervalMs = 350)

    override fun getSessions(sessionType: String?, year: Int?, country: String?): List<F1Event> {
        rateLimiter.acquire()
        val sessions = openF1RestClient.get()
            .uri { builder ->
                builder.path("/sessions")
                sessionType?.let { builder.queryParam("session_type", it) }
                year?.let { builder.queryParam("year", it) }
                country?.let { builder.queryParam("country_name", it) }
                builder.build()
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<List<OpenF1SessionDto>>() {})
            ?: emptyList()

        return sessions.map { it.toDomain() }
    }

    override fun getDrivers(sessionKey: Long): List<Driver> {
        rateLimiter.acquire()
        val drivers = openF1RestClient.get()
            .uri { builder ->
                builder.path("/drivers")
                    .queryParam("session_key", sessionKey)
                    .build()
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<List<OpenF1DriverDto>>() {})
            ?: emptyList()

        return drivers.map { it.toDomain() }
    }
}

private class SimpleRateLimiter(private val minIntervalMs: Long) {
    @Volatile
    private var lastRequestTime = 0L

    @Synchronized
    fun acquire() {
        val now = System.currentTimeMillis()
        val elapsed = now - lastRequestTime
        if (elapsed < minIntervalMs) {
            Thread.sleep(minIntervalMs - elapsed)
        }
        lastRequestTime = System.currentTimeMillis()
    }
}

private fun OpenF1SessionDto.toDomain() = F1Event(
    sessionKey = sessionKey,
    sessionName = sessionName,
    sessionType = sessionType,
    countryName = countryName,
    year = year,
    dateStart = dateStart
)

private fun OpenF1DriverDto.toDomain() = Driver(
    driverId = driverNumber,
    fullName = fullName
)
