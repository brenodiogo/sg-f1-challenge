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
import org.mockito.kotlin.whenever
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ListEventsServiceTest {

    @Mock
    lateinit var f1EventProvider: F1EventProvider

    @InjectMocks
    lateinit var service: ListEventsService

    private val session = F1Event(
        sessionKey = 9001,
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
        whenever(f1EventProvider.getSessions(null, null, null)).thenReturn(listOf(session))
        whenever(f1EventProvider.getDrivers(9001)).thenReturn(drivers)

        val result = service.listEvents(null, null, null)

        assertEquals(1, result.size)
        assertEquals(session, result[0].event)
        assertEquals(2, result[0].market.size)
        assertEquals("Lewis Hamilton", result[0].market[0].driver.fullName)
        assertEquals("Max Verstappen", result[0].market[1].driver.fullName)
    }

    @Test
    fun `odds are always in valid range`() {
        whenever(f1EventProvider.getSessions(null, null, null)).thenReturn(listOf(session))
        whenever(f1EventProvider.getDrivers(9001)).thenReturn(drivers)

        val validOdds = setOf(2, 3, 4)
        repeat(50) {
            val result = service.listEvents(null, null, null)
            result.flatMap { it.market }.forEach { driverOdds ->
                assertTrue(driverOdds.odds in validOdds)
            }
        }
    }

    @Test
    fun `empty sessions returns empty result`() {
        whenever(f1EventProvider.getSessions(null, null, null)).thenReturn(emptyList())

        val result = service.listEvents(null, null, null)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `passes filter parameters to provider`() {
        whenever(f1EventProvider.getSessions("Race", 2024, "Monaco")).thenReturn(listOf(session))
        whenever(f1EventProvider.getDrivers(9001)).thenReturn(drivers)

        val result = service.listEvents("Race", 2024, "Monaco")

        assertEquals(1, result.size)
    }
}
