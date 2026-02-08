package com.sgchallenge.f1.infrastructure.adapter.outbound.persistence

import com.sgchallenge.f1.domain.model.User
import com.sgchallenge.f1.domain.port.outbound.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserRepositoryAdapter(
    private val jpa: UserJpaRepository
) : UserRepository {

    override fun findAll(): List<User> =
        jpa.findAll().map { it.toDomain() }

    override fun findById(id: UUID): User? =
        jpa.findById(id).orElse(null)?.toDomain()

    override fun save(user: User): User =
        jpa.save(user.toEntity()).toDomain()
}

private fun UserJpaEntity.toDomain() = User(id = id, balance = balance)

private fun User.toEntity() = UserJpaEntity(id = id, balance = balance)
