package com.sgchallenge.f1.domain.model

import java.time.Instant

data class F1Event(
    val sessionKey: Long,
    val sessionName: String,
    val sessionType: String,
    val countryName: String,
    val year: Int,
    val dateStart: Instant
)
