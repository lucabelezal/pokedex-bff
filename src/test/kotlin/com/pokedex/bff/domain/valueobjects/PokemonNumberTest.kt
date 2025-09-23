package com.pokedex.bff.domain.valueobjects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for PokemonNumber value object
 * 
 * Demonstrates testing pure domain logic without any infrastructure dependencies.
 * These tests focus on business rules and value object behavior.
 */
class PokemonNumberTest {

    @Test
    fun `should create valid pokemon number from string`() {
        // Given & When
        val pokemonNumber = PokemonNumber("025")

        // Then
        assertThat(pokemonNumber.value).isEqualTo("025")
        assertThat(pokemonNumber.isValid()).isTrue()
    }

    @Test
    fun `should create valid pokemon number from integer`() {
        // Given & When
        val pokemonNumber = PokemonNumber.fromInt(25)

        // Then
        assertThat(pokemonNumber.value).isEqualTo("25")
        assertThat(pokemonNumber.toNumeric()).isEqualTo(25)
    }

    @Test
    fun `should format number for display correctly`() {
        // Given
        val pokemonNumber = PokemonNumber("25")

        // When
        val formatted = pokemonNumber.formatForDisplay()

        // Then
        assertThat(formatted).isEqualTo("025")
    }

    @Test
    fun `should format four digit number correctly`() {
        // Given
        val pokemonNumber = PokemonNumber("1010")

        // When
        val formatted = pokemonNumber.formatForDisplay()

        // Then
        assertThat(formatted).isEqualTo("1010") // Should not pad 4-digit numbers
    }

    @Test
    fun `should create display string with prefix`() {
        // Given
        val pokemonNumber = PokemonNumber("1")

        // When
        val displayString = pokemonNumber.toDisplayString()

        // Then
        assertThat(displayString).isEqualTo("Nº001")
    }

    @Test
    fun `should identify generation 1 pokemon correctly`() {
        // Given
        val gen1Pokemon = PokemonNumber("150") // Mewtwo
        val gen2Pokemon = PokemonNumber("152") // Chikorita

        // When & Then
        assertThat(gen1Pokemon.isGeneration1()).isTrue()
        assertThat(gen2Pokemon.isGeneration1()).isFalse()
    }

    @Test
    fun `should convert to numeric correctly`() {
        // Given
        val pokemonNumber = PokemonNumber("123")

        // When
        val numeric = pokemonNumber.toNumeric()

        // Then
        assertThat(numeric).isEqualTo(123)
    }

    @Test
    fun `should throw exception for blank number`() {
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            PokemonNumber("")
        }
        
        assertThat(exception.message).isEqualTo("Pokemon number cannot be blank")
    }

    @Test
    fun `should throw exception for non-numeric string`() {
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            PokemonNumber("abc")
        }
        
        assertThat(exception.message).contains("Pokemon number must be 3-4 digits")
    }

    @Test
    fun `should throw exception for negative number`() {
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            PokemonNumber.fromInt(-1)
        }
        
        assertThat(exception.message).contains("Pokemon number must be positive")
    }

    @Test
    fun `should throw exception for zero`() {
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            PokemonNumber.fromInt(0)
        }
        
        assertThat(exception.message).contains("Pokemon number must be positive")
    }

    @Test
    fun `should create unknown pokemon number`() {
        // Given & When
        val unknownNumber = PokemonNumber.unknown()

        // Then
        assertThat(unknownNumber.value).isEqualTo("0")
        assertThat(unknownNumber.toDisplayString()).isEqualTo("Nº000")
    }

    @Test
    fun `should validate various numeric formats`() {
        // Given & When & Then
        assertThat(PokemonNumber("1").isValid()).isTrue()
        assertThat(PokemonNumber("25").isValid()).isTrue()
        assertThat(PokemonNumber("150").isValid()).isTrue()
        assertThat(PokemonNumber("1010").isValid()).isTrue()
    }

    @Test
    fun `should reject invalid formats`() {
        // When & Then
        assertThrows<IllegalArgumentException> { PokemonNumber("12345") } // Too many digits
        assertThrows<IllegalArgumentException> { PokemonNumber("0") }     // Zero not allowed
        assertThrows<IllegalArgumentException> { PokemonNumber("01a") }   // Contains letters
        assertThrows<IllegalArgumentException> { PokemonNumber(" 25 ") }  // Contains spaces
    }
}