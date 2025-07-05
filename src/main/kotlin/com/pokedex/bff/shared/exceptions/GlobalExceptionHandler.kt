package com.pokedex.bff.shared.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiError> {
        val message = "Parâmetro '${ex.name}' inválido: ${ex.value}."
        logger.warn(message, ex)
        return buildResponse(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ApiError> {
        val message = "Parâmetro obrigatório '${ex.parameterName}' não foi informado."
        logger.warn(message, ex)
        return buildResponse(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }
        logger.warn("Erro de validação: $errors", ex)
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Erro de validação: $errors"
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiError> {
        logger.warn("Argumento inválido: ${ex.message}", ex)
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "Requisição inválida."
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception): ResponseEntity<ApiError> {
        logger.error("Erro interno inesperado: ${ex.message}", ex)
        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro interno. Tente novamente mais tarde."
        )
    }

    private fun buildResponse(status: HttpStatus, message: String): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError(status.value(), message), status)
    }

    data class ApiError(
        val status: Int,
        val message: String
    )
}