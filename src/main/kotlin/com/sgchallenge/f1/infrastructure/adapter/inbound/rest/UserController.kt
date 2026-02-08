package com.sgchallenge.f1.infrastructure.adapter.inbound.rest

import com.sgchallenge.f1.domain.exception.UserNotFoundException
import com.sgchallenge.f1.domain.port.outbound.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository
) {

    @GetMapping
    fun listUsers(): List<UserResponse> =
        userRepository.findAll().map { UserResponse(it.id, it.balance) }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserResponse {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException(userId)
        return UserResponse(user.id, user.balance)
    }

    data class UserResponse(val id: UUID, val balance: BigDecimal)
}
