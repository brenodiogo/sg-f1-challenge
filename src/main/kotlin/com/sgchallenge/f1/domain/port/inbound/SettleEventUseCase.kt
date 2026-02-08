package com.sgchallenge.f1.domain.port.inbound

interface SettleEventUseCase {
    fun settleEvent(eventId: Long, winnerDriverId: Int)
}
