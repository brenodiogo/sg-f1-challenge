package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id val id: UUID = UUID.randomUUID(),
    var balance: BigDecimal = BigDecimal.ZERO
)
