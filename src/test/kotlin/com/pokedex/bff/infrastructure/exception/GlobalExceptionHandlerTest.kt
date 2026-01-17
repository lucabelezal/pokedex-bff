package com.pokedex.bff.infrastructure.exception

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.pokedex.bff.domain.pokemon.exception.InvalidPokemonException
import com.pokedex.bff.domain.pokemon.exception.PokemonNotFoundException
import com.pokedex.bff.domain.trainer.exception.TrainerNotFoundException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError as SpringFieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest
import java.lang.reflect.UndeclaredThrowableException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertNull

class GlobalExceptionHandlerTest {

    private lateinit var webRequest: WebRequest

    @BeforeEach
    fun setup() {
        webRequest = mockk(relaxed = true)
    }
    
    private fun createHandlerWithProfile(profile: String): GlobalExceptionHandler {
        val handler = GlobalExceptionHandler()
        val field = GlobalExceptionHandler::class.java.getDeclaredField("activeProfile")
        field.isAccessible = true
        field.set(handler, profile)
        return handler
    }

    private fun mismatchedInputException(message: String = "Failed to read JSON"): MismatchedInputException {
        val mapper = jacksonObjectMapper()
        val parser = mapper.factory.createParser("{}")
        val javaType = mapper.constructType(Any::class.java)
        return MismatchedInputException.from(parser, javaType, message)
    }

    @Nested
    inner class DevelopmentMode {
        private lateinit var devHandler: GlobalExceptionHandler
        
        @BeforeEach
        fun setupDevMode() {
            devHandler = createHandlerWithProfile("dev")
        }

        @Test
        fun `deve incluir detalhes completos em MismatchedInputException no modo dev`() {
            val exception = mismatchedInputException()
            exception.stackTrace = arrayOf(
                StackTraceElement("TestClass", "testMethod", "TestClass.kt", 10)
            )

            val response = devHandler.handleMismatchedInput(exception, webRequest)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            val body = response.body!!
            assertEquals("DESERIALIZATION_ERROR", body.code)
            assertTrue(body.message.contains("Failed to deserialize JSON"))
            assertNotNull(body.details)
            assertTrue(body.details!!.containsKey("stackTrace"))
        }

        @Test
        fun `deve incluir stackTrace em IllegalArgumentException no modo dev`() {
            val exception = IllegalArgumentException("Invalid Pokemon name")

            val response = devHandler.handleIllegalArgument(exception, webRequest)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            val body = response.body!!
            assertEquals("INVALID_REQUEST", body.code)
            assertEquals("Invalid Pokemon name", body.message)
            assertNotNull(body.details)
            assertTrue(body.details!!.containsKey("stackTrace"))
        }
    }

    @Nested
    inner class ProductionMode {
        private lateinit var prodHandler: GlobalExceptionHandler
        
        @BeforeEach
        fun setupProdMode() {
            prodHandler = createHandlerWithProfile("prod")
        }

        @Test
        fun `deve ocultar detalhes em MismatchedInputException no modo prod`() {
            val exception = mismatchedInputException()
            exception.stackTrace = arrayOf(
                StackTraceElement("TestClass", "testMethod", "TestClass.kt", 10)
            )

            val response = prodHandler.handleMismatchedInput(exception, webRequest)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            val body = response.body!!
            assertEquals("DESERIALIZATION_ERROR", body.code)
            assertEquals("Invalid data format", body.message)
            assertNotNull(body.details)
            assertEquals(1, body.details!!.size)
            assertTrue(body.details!!.containsKey("exception"))
        }
    }

    @Nested
    inner class DomainExceptions {
        private lateinit var handler: GlobalExceptionHandler
        
        @BeforeEach
        fun setupHandler() {
            handler = createHandlerWithProfile("prod")
        }

        @Test
        fun `deve tratar PokemonNotFoundException com status 404`() {
            val exception = PokemonNotFoundException("Pokemon with ID 999 not found")

            val response = handler.handlePokemonNotFound(exception, webRequest)

            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
            val body = response.body!!
            assertEquals("POKEMON_NOT_FOUND", body.code)
            assertEquals("Pokemon with ID 999 not found", body.message)
            assertNull(body.details)
        }

        @Test
        fun `deve tratar InvalidPokemonException com status 400`() {
            val exception = InvalidPokemonException("Pokemon name cannot be blank")

            val response = handler.handleInvalidPokemon(exception, webRequest)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            val body = response.body!!
            assertEquals("INVALID_POKEMON", body.code)
            assertEquals("Pokemon name cannot be blank", body.message)
            assertNull(body.details)
        }
    }

    @Nested
    inner class ValidationExceptions {
        private lateinit var handler: GlobalExceptionHandler
        
        @BeforeEach
        fun setupHandler() {
            handler = createHandlerWithProfile("prod")
        }

        @Test
        fun `deve tratar MethodArgumentNotValidException com detalhes dos campos`() {
            val bindingResult = mockk<BindingResult>()
            val fieldError1 = SpringFieldError("pokemonRequest", "name", "", false, null, null, "must not be blank")
            
            every { bindingResult.fieldErrors } returns listOf(fieldError1)
            
            val exception = MethodArgumentNotValidException(mockk(relaxed = true), bindingResult)

            val response = handler.handleValidationErrors(exception, webRequest)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            val body = response.body!!
            assertEquals("VALIDATION_ERROR", body.code)
            assertEquals("Validation failed for one or more fields", body.message)
            assertEquals(1, body.fieldErrors.size)
        }

        @Test
        fun `deve omitir rejectedValue para campos sensíveis em produção`() {
            val bindingResult = mockk<BindingResult>()
            val fieldError = SpringFieldError("userRequest", "password", "secret123", false, null, null, "password too weak")
            
            every { bindingResult.fieldErrors } returns listOf(fieldError)
            
            val exception = MethodArgumentNotValidException(mockk(relaxed = true), bindingResult)

            val response = handler.handleValidationErrors(exception, webRequest)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            val body = response.body!!
            val passwordError = body.fieldErrors.find { it.field == "password" }
            assertNotNull(passwordError)
            assertNull(passwordError.rejectedValue)
        }
    }

    @Nested
    inner class UndeclaredThrowableExceptionHandling {
        private lateinit var handler: GlobalExceptionHandler
        
        @BeforeEach
        fun setupHandler() {
            handler = createHandlerWithProfile("prod")
        }

        @Test
        fun `deve desembrulhar e tratar PokemonNotFoundException`() {
            val cause = PokemonNotFoundException("Pokemon #1 not found")
            val exception = UndeclaredThrowableException(cause)

            val response = handler.handleUndeclaredThrowable(exception, webRequest)

            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
            val body = response.body!!
            assertEquals("POKEMON_NOT_FOUND", body.code)
        }
    }
}
