package br.com.financialcontrol.adapters.input.web.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiErrorHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalid(error: RuntimeException) = mapOf("message" to (error.message ?: "Invalid request"))

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(error: NoSuchElementException) = mapOf("message" to (error.message ?: "Resource not found"))
}
