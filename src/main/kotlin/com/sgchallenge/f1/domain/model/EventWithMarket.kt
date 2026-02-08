package com.sgchallenge.f1.domain.model

data class EventWithMarket(val event: F1Event, val market: List<DriverOdds>)
