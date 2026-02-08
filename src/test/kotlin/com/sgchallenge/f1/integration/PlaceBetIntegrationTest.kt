package com.sgchallenge.f1.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.util.UUID

class PlaceBetIntegrationTest : BaseIntegrationTest() {

    private val userId = UUID.fromString("00000000-0000-0000-0000-000000000001")

    @Test
    fun `place bet debits user and creates pending bet`() {
        val response = restTemplate.postForEntity(
            "/api/bets",
            mapOf("userId" to userId, "eventId" to 9222, "driverId" to 1, "amount" to 25, "odds" to 3),
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body!!["status"]).isEqualTo("PENDING")
        assertThat(BigDecimal(response.body!!["amount"].toString())).isEqualByComparingTo(BigDecimal("25"))

        val balance = jdbcTemplate.queryForObject(
            "SELECT balance FROM users WHERE id = ?", BigDecimal::class.java, userId
        )
        assertThat(balance).isEqualByComparingTo(BigDecimal("75.00"))
    }

    @Test
    fun `insufficient balance returns 422 and balance is unchanged`() {
        val response = restTemplate.postForEntity(
            "/api/bets",
            mapOf("userId" to userId, "eventId" to 9222, "driverId" to 1, "amount" to 150, "odds" to 2),
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)

        val balance = jdbcTemplate.queryForObject(
            "SELECT balance FROM users WHERE id = ?", BigDecimal::class.java, userId
        )
        assertThat(balance).isEqualByComparingTo(BigDecimal("100.00"))
    }

    @Test
    fun `non-existent user returns 404`() {
        val response = restTemplate.postForEntity(
            "/api/bets",
            mapOf("userId" to UUID.randomUUID(), "eventId" to 9222, "driverId" to 1, "amount" to 10, "odds" to 2),
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
