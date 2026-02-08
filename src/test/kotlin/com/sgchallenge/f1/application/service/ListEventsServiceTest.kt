package com.sgchallenge.f1.application.service

import com.sgchallenge.f1.domain.model.Driver
import com.sgchallenge.f1.domain.model.F1Event
import com.sgchallenge.f1.domain.port.outbound.F1EventProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ListEventsServiceTest {

    @Mock
    lateinit var f1EventProvider: F1EventProvider

    @InjectMocks
    lateinit var service: ListEventsService

    private fun session(key: Long) = F1Event(
        sessionKey = key,
        sessionName = "Race",
        sessionType = "Race",
        countryName = "Monaco",
        year = 2024,
        dateStart = Instant.parse("2024-05-26T13:00:00Z")
    )

    private val drivers = listOf(
        Driver(44, "Lewis Hamilton"),
        Driver(1, "Max Verstappen")
    )

    @Test
    fun `returns events with driver market`() {
        whenever(f1EventProvider.getSessions(null, null, null)).thenReturn(listOf(session(9001)))
        whenever(f1EventProvider.getDrivers(9001)).thenReturn(drivers)

        val result = service.listEvents(null, null, null, 0, 10)

        assertEquals(1, result.content.size)
        assertEquals(9001L, result.content[0].event.sessionKey)
        assertEquals(2, result.content[0].market.size)
        assertEquals(1, result.totalElements)
    }

    @Test
    fun `odds are always in valid range`() {
        whenever(f1EventProvider.getSessions(null, null, null)).thenReturn(listOf(session(9001)))
        whenever(f1EventProvider.getDrivers(9001)).thenReturn(drivers)

        val validOdds = setOf(2, 3, 4)
        repeat(50) {
            val result = service.listEvents(null, null, null, 0, 10)
            result.content.flatMap { it.market }.forEach { driverOdds ->
                assertTrue(driverOdds.odds in validOdds)
            }
        }
    }

    @Test
    fun `empty sessions returns empty result`() {
        whenever(f1EventProvider.getSessions(null, null, null)).thenReturn(emptyList())

        val result = service.listEvents(null, null, null, 0, 10)

        assertTrue(result.content.isEmpty())
        assertEquals(0, result.totalElements)
    }

    @Test
    fun `passes filter parameters to provider`() {
        whenever(f1EventProvider.getSessions("Race", 2024, "Monaco")).thenReturn(listOf(session(9001)))
        whenever(f1EventProvider.getDrivers(9001)).thenReturn(drivers)

        val result = service.listEvents("Race", 2024, "Monaco", 0, 10)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `pagination only fetches drivers for current page`() {
        val sessions = (1L..10L).map { session(it) }
        whenever(f1EventProvider.getSessions(null, null, null)).thenReturn(sessions)
        whenever(f1EventProvider.getDrivers(1)).thenReturn(drivers)
        whenever(f1EventProvider.getDrivers(2)).thenReturn(drivers)

        val result = service.listEvents(null, null, null, 0, 2)

        assertEquals(2, result.content.size)
        assertEquals(10, result.totalElements)
        assertEquals(0, result.page)
        assertEquals(2, result.size)
        verify(f1EventProvider, never()).getDrivers(3)
    }
}
