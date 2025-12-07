package com.pokedex.bff.infrastructure.exception

import java.time.Instant

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val details: Map<String, Any?>? = null
)

data class ValidationErrorResponse(
    val code: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val fieldErrors: List<FieldError>
)

data class FieldError(
    val field: String,
    val message: String,
    val rejectedValue: Any?
)
