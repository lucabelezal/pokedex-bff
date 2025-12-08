package com.pokedex.bff.infrastructure.exception

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.pokedex.bff.domain.pokemon.exception.InvalidPokemonException
import com.pokedex.bff.domain.pokemon.exception.PokemonNotFoundException
import com.pokedex.bff.domain.trainer.exception.TrainerNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.lang.reflect.UndeclaredThrowableException
import java.time.Instant

/**
 * Global exception handler para tratar todas as exceções da aplicação.
 * Centraliza o tratamento de erros e garante respostas consistentes.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    @Value("\${spring.profiles.active:prod}")
    private lateinit var activeProfile: String
    
    private fun isDevelopmentMode(): Boolean = activeProfile == "dev"

    /**
     * Trata UndeclaredThrowableException (wraps exceptions from proxy methods)
     */
    @ExceptionHandler(UndeclaredThrowableException::class)
    fun handleUndeclaredThrowable(
        ex: UndeclaredThrowableException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val cause = ex.undeclaredThrowable ?: ex.cause
        logger.error("Undeclared throwable occurred", cause)
        
        // Tenta tratar a causa real se possível
        return when (cause) {
            is PokemonNotFoundException -> handlePokemonNotFound(cause, request)
            is TrainerNotFoundException -> handleTrainerNotFound(cause, request)
            is InvalidPokemonException -> handleInvalidPokemon(cause, request)
            is IllegalArgumentException -> handleIllegalArgument(cause, request)
            else -> handleUnexpectedException(cause ?: ex, request)
        }
    }

    /**
     * Trata MismatchedInputException (Jackson deserialization errors)
     */
    @ExceptionHandler(MismatchedInputException::class)
    fun handleMismatchedInput(
        ex: MismatchedInputException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("JSON deserialization error: ${ex.message}", ex)
        
        val errorResponse = ErrorResponse(
            code = "DESERIALIZATION_ERROR",
            message = if (isDevelopmentMode()) {
                "Failed to deserialize JSON: ${ex.originalMessage}"
            } else {
                "Invalid data format"
            },
            timestamp = Instant.now(),
            details = if (isDevelopmentMode()) {
                mapOf(
                    "exception" to ex.javaClass.simpleName,
                    "path" to (ex.path?.joinToString(".") { it.fieldName ?: "[${it.index}]" } ?: "unknown"),
                    "targetType" to (ex.targetType?.toString() ?: "unknown"),
                    "stackTrace" to ex.stackTrace.take(5).map { it.toString() }
                )
            } else {
                mapOf("exception" to ex.javaClass.simpleName)
            }
        )
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }

    /**
     * Trata exceções de Pokémon não encontrado
     */
    @ExceptionHandler(PokemonNotFoundException::class)
    fun handlePokemonNotFound(
        ex: PokemonNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Pokemon not found: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            code = "POKEMON_NOT_FOUND",
            message = ex.message ?: "Pokemon not found",
            timestamp = Instant.now()
        )
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }

    /**
     * Trata exceções de Trainer não encontrado
     */
    @ExceptionHandler(TrainerNotFoundException::class)
    fun handleTrainerNotFound(
        ex: TrainerNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Trainer not found: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            code = "TRAINER_NOT_FOUND",
            message = ex.message ?: "Trainer not found",
            timestamp = Instant.now()
        )
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }

    /**
     * Trata exceções de dados inválidos do Pokémon
     */
    @ExceptionHandler(InvalidPokemonException::class)
    fun handleInvalidPokemon(
        ex: InvalidPokemonException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid pokemon data: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            code = "INVALID_POKEMON",
            message = ex.message ?: "Invalid pokemon data",
            timestamp = Instant.now()
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    /**
     * Trata exceções de validação de requisitos (require, check)
     */
    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class)
    fun handleIllegalArgument(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid argument or state: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            code = "INVALID_REQUEST",
            message = ex.message ?: "Invalid request",
            timestamp = Instant.now(),
            details = if (isDevelopmentMode()) {
                mapOf(
                    "exception" to ex.javaClass.simpleName,
                    "stackTrace" to ex.stackTrace.take(5).map { it.toString() }
                )
            } else {
                mapOf("exception" to ex.javaClass.simpleName)
            }
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    /**
     * Trata exceções de validação do Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ValidationErrorResponse> {
        logger.warn("Validation error: {}", ex.message)
        
        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            FieldError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "Invalid value",
                rejectedValue = if (isDevelopmentMode() && !isSensitiveField(fieldError.field)) {
                    fieldError.rejectedValue
                } else {
                    null // Omite em produção ou para campos sensíveis
                }
            )
        }
        
        val errorResponse = ValidationErrorResponse(
            code = "VALIDATION_ERROR",
            message = "Validation failed for one or more fields",
            timestamp = Instant.now(),
            fieldErrors = fieldErrors
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }
    
    /**
     * Verifica se o campo contém informações sensíveis que não devem ser expostas.
     */
    private fun isSensitiveField(fieldName: String): Boolean {
        val sensitiveFields = setOf(
            "password", "senha", "pass", "pwd",
            "token", "apikey", "api_key", "secret",
            "creditcard", "credit_card", "cvv", "cardnumber",
            "ssn", "cpf", "rg",
            "authorization", "bearer"
        )
        return sensitiveFields.any { fieldName.lowercase().contains(it) }
    }

    /**
     * Trata exceções genéricas não capturadas
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        return handleUnexpectedException(ex, request)
    }

    /**
     * Método auxiliar para tratar exceções inesperadas
     */
    private fun handleUnexpectedException(
        ex: Throwable,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        
        val errorResponse = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = if (isDevelopmentMode()) {
                "Unexpected error: ${ex.message ?: ex.javaClass.simpleName}"
            } else {
                "An unexpected error occurred. Please try again later."
            },
            timestamp = Instant.now(),
            details = if (isDevelopmentMode()) {
                mapOf(
                    "exception" to ex.javaClass.simpleName,
                    "message" to (ex.message ?: "No message"),
                    "stackTrace" to ex.stackTrace.take(10).map { it.toString() }
                )
            } else {
                mapOf("exception" to ex.javaClass.simpleName)
            }
        )
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }
}
