package com.sgchallenge.f1.domain.port.outbound

import com.sgchallenge.f1.domain.model.User
import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): User?
    fun save(user: User): User
}
