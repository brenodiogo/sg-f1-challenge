package com.sgchallenge.f1.domain.port.outbound

import com.sgchallenge.f1.domain.model.Driver
import com.sgchallenge.f1.domain.model.F1Event

interface F1EventProvider {
    fun getSessions(sessionType: String?, year: Int?, country: String?): List<F1Event>
    fun getDrivers(sessionKey: Long): List<Driver>
}
