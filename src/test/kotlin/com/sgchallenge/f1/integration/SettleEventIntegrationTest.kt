package com.sgchallenge.f1.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.util.UUID

class SettleEventIntegrationTest : BaseIntegrationTest() {

    private val user1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val user2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

    @Test
    fun `settle event credits winner and marks bets correctly`() {
        placeBet(user1, eventId = 9222, driverId = 1, amount = 30, odds = 3)
        placeBet(user2, eventId = 9222, driverId = 2, amount = 20, odds = 2)

        val response = restTemplate.postForEntity(
            "/api/event-outcomes",
            mapOf("eventId" to 9222, "winnerDriverId" to 1),
            Void::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        @Suppress("UNCHECKED_CAST")
        val bets = restTemplate.getForObject("/api/bets?eventId=9222", List::class.java) as List<Map<String, Any>>
        val user1Bet = bets.first { it["userId"].toString().endsWith("001") }
        val user2Bet = bets.first { it["userId"].toString().endsWith("002") }
        assertThat(user1Bet["status"]).isEqualTo("WON")
        assertThat(user2Bet["status"]).isEqualTo("LOST")

        val balance1 = jdbcTemplate.queryForObject("SELECT balance FROM users WHERE id = ?", BigDecimal::class.java, user1)
        val balance2 = jdbcTemplate.queryForObject("SELECT balance FROM users WHERE id = ?", BigDecimal::class.java, user2)
        assertThat(balance1).isEqualByComparingTo(BigDecimal("160.00")) // 100 - 30 + 90
        assertThat(balance2).isEqualByComparingTo(BigDecimal("80.00"))  // 100 - 20
    }

    @Test
    fun `duplicate settlement returns 409`() {
        placeBet(user1, eventId = 9222, driverId = 1, amount = 10, odds = 2)
        restTemplate.postForEntity("/api/event-outcomes", mapOf("eventId" to 9222, "winnerDriverId" to 1), Void::class.java)

        val response = restTemplate.postForEntity(
            "/api/event-outcomes",
            mapOf("eventId" to 9222, "winnerDriverId" to 1),
            Map::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `settle event with no bets records outcome`() {
        restTemplate.postForEntity("/api/event-outcomes", mapOf("eventId" to 9222, "winnerDriverId" to 5), Void::class.java)

        val outcome = restTemplate.getForObject("/api/event-outcomes/9222", Map::class.java)!!
        assertThat(outcome["eventId"]).isEqualTo(9222)
        assertThat(outcome["winnerDriverId"]).isEqualTo(5)
    }

    private fun placeBet(userId: UUID, eventId: Long, driverId: Int, amount: Int, odds: Int) {
        restTemplate.postForEntity(
            "/api/bets",
            mapOf("userId" to userId, "eventId" to eventId, "driverId" to driverId, "amount" to amount, "odds" to odds),
            Map::class.java
        )
    }
}
