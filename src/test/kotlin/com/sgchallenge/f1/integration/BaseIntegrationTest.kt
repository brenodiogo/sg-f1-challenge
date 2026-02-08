package com.sgchallenge.f1.integration

import com.sgchallenge.f1.domain.port.outbound.F1EventProvider
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate

    @MockitoBean
    protected lateinit var f1EventProvider: F1EventProvider

    @BeforeEach
    fun resetDatabase() {
        jdbcTemplate.execute("DELETE FROM event_outcomes")
        jdbcTemplate.execute("DELETE FROM bets")
        jdbcTemplate.execute("UPDATE users SET balance = 100.00")
    }
}
