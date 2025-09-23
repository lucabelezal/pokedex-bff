package com.pokedex.bff.domain.valueobjects

/**
 * Value Object representing a Pokemon's number (National Dex number)
 * 
 * Encapsulates the business rules for Pokemon numbers, including validation
 * and formatting requirements. This is pure domain logic with no infrastructure dependencies.
 */
@JvmInline
value class PokemonNumber(val value: String) {
    
    init {
        require(value.isNotBlank()) { "Pokemon number cannot be blank" }
        require(isValidFormat(value)) { "Pokemon number must be 3-4 digits, but was: $value" }
    }
    
    /**
     * Formats the Pokemon number for display (e.g., "001", "1010")
     */
    fun formatForDisplay(): String {
        return value.padStart(3, '0')
    }
    
    /**
     * Converts to formatted display string with "Nº" prefix
     */
    fun toDisplayString(): String {
        return "Nº${formatForDisplay()}"
    }
    
    /**
     * Converts the Pokemon number to its numeric representation
     */
    fun toNumeric(): Int {
        return value.toIntOrNull() ?: throw IllegalStateException("Invalid Pokemon number format: $value")
    }
    
    /**
     * Checks if this is a valid Pokemon number according to business rules
     */
    fun isValid(): Boolean {
        return value.isNotBlank() && isValidFormat(value)
    }
    
    /**
     * Checks if this Pokemon number represents a Generation I Pokemon (001-151)
     */
    fun isGeneration1(): Boolean {
        val numeric = toNumeric()
        return numeric in 1..151
    }
    
    private fun isValidFormat(number: String): Boolean {
        return number.matches(Regex("\\d{1,4}")) && number.toIntOrNull()?.let { it > 0 } == true
    }
    
    companion object {
        /**
         * Creates a PokemonNumber from an integer value
         */
        fun fromInt(number: Int): PokemonNumber {
            require(number > 0) { "Pokemon number must be positive, but was: $number" }
            return PokemonNumber(number.toString())
        }
        
        /**
         * Creates a default "unknown" Pokemon number
         */
        fun unknown(): PokemonNumber {
            return PokemonNumber("0")
        }
    }
}