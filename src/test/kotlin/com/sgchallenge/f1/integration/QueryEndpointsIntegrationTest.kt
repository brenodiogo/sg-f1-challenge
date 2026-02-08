package com.sgchallenge.f1.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.util.UUID

class QueryEndpointsIntegrationTest : BaseIntegrationTest() {

    private val user1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val user2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `GET users returns seeded users with balances`() {
        val response = restTemplate.getForEntity("/api/users", List::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val users = response.body as List<Map<String, Any>>
        assertThat(users).hasSize(3)
        assertThat(users.all { BigDecimal(it["balance"].toString()).compareTo(BigDecimal("100.00")) == 0 }).isTrue()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `GET bets filtered by status returns correct subsets`() {
        placeBet(user1, eventId = 9222, driverId = 1, amount = 10, odds = 2)
        placeBet(user2, eventId = 9222, driverId = 2, amount = 15, odds = 3)
        restTemplate.postForEntity("/api/event-outcomes", mapOf("eventId" to 9222, "winnerDriverId" to 1), Void::class.java)

        val won = restTemplate.getForObject("/api/bets?status=WON", List::class.java) as List<Map<String, Any>>
        val lost = restTemplate.getForObject("/api/bets?status=LOST", List::class.java) as List<Map<String, Any>>

        assertThat(won).hasSize(1)
        assertThat(won[0]["driverId"]).isEqualTo(1)
        assertThat(lost).hasSize(1)
        assertThat(lost[0]["driverId"]).isEqualTo(2)
    }

    private fun placeBet(userId: UUID, eventId: Long, driverId: Int, amount: Int, odds: Int) {
        restTemplate.postForEntity(
            "/api/bets",
            mapOf("userId" to userId, "eventId" to eventId, "driverId" to driverId, "amount" to amount, "odds" to odds),
            Map::class.java
        )
    }
}
