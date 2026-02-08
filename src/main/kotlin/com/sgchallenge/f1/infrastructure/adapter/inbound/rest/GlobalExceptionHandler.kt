package com.sgchallenge.f1.infrastructure.adapter.inbound.rest

import com.sgchallenge.f1.domain.exception.BetAlreadySettledException
import com.sgchallenge.f1.domain.exception.EventAlreadySettledException
import com.sgchallenge.f1.domain.exception.InsufficientBalanceException
import com.sgchallenge.f1.domain.exception.InvalidAmountException
import com.sgchallenge.f1.domain.exception.InvalidOddsException
import com.sgchallenge.f1.domain.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handle(ex: UserNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "User not found")

    @ExceptionHandler(InsufficientBalanceException::class)
    fun handle(ex: InsufficientBalanceException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message ?: "Insufficient balance")

    @ExceptionHandler(EventAlreadySettledException::class)
    fun handle(ex: EventAlreadySettledException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Event already settled")

    @ExceptionHandler(BetAlreadySettledException::class)
    fun handle(ex: BetAlreadySettledException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Bet already settled")

    @ExceptionHandler(InvalidAmountException::class, InvalidOddsException::class)
    fun handleValidation(ex: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid input")

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleUpstream(ex: HttpClientErrorException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "External F1 API error: ${ex.statusCode}")
}
