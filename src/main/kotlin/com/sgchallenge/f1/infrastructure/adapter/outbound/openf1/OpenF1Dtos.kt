package com.sgchallenge.f1.infrastructure.adapter.outbound.openf1

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenF1SessionDto(
    @JsonProperty("session_key") val sessionKey: Long,
    @JsonProperty("session_name") val sessionName: String,
    @JsonProperty("session_type") val sessionType: String,
    @JsonProperty("country_name") val countryName: String,
    @JsonProperty("year") val year: Int,
    @JsonProperty("date_start") val dateStart: Instant
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenF1DriverDto(
    @JsonProperty("driver_number") val driverNumber: Int,
    @JsonProperty("full_name") val fullName: String
)
