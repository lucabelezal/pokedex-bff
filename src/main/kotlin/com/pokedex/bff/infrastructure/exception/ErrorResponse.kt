package com.pokedex.bff.infrastructure.exception

import com.fasterxml.jackson.annotation.JsonInclude
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

/**
 * Representa um erro de validação em um campo específico.
 * 
 * **Segurança:** O campo `rejectedValue` só deve ser incluído em ambiente de desenvolvimento
 * para evitar vazamento de informações sensíveis (senhas, tokens, etc.) em produção.
 */
data class FieldError(
    val field: String,
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val rejectedValue: Any? = null
)
