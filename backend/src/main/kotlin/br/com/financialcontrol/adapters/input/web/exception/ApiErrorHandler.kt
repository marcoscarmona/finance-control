package br.com.financialcontrol.adapters.input.web.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiErrorHandler {
    @ExceptionHandler(IllegalArgumentException::class, NoSuchElementException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalid(error: RuntimeException) = mapOf("message" to (error.message ?: "Invalid request"))
}
