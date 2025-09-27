package com.pokedex.bff.domain.valueobjects

/**
 * Value Object representing a Pokemon's number (National Dex number)
 */
@JvmInline
value class PokemonNumber(val value: String) {

    init {
        require(value.isNotBlank()) { "Pokemon number cannot be blank" }
        require(isValidFormat(value)) { "Pokemon number must be 1-4 digits, but was: $value" }
    }

    fun formatForDisplay(): String = value.padStart(PADDING_WIDTH, PADDING_CHAR)

    fun toDisplayString(): String = "Nº${formatForDisplay()}"

    fun toNumeric(): Int = value.toIntOrNull() ?: error("Invalid Pokemon number format: $value")

    fun isValid(): Boolean = value.isNotBlank() && isValidFormat(value)

    fun isGeneration1(): Boolean {
        val numeric = toNumeric()
        return numeric in G1_START..G1_END
    }

    private fun isValidFormat(number: String): Boolean {
        return number.matches(Regex("\\d{1,4}")) && number.toIntOrNull()?.let { it > 0 } == true
    }

    companion object {
        private const val PADDING_WIDTH = 3
        private const val PADDING_CHAR = '0'

        private const val G1_START = 1
        private const val G1_END = 151

        fun fromInt(number: Int): PokemonNumber {
            require(number > 0) { "Pokemon number must be positive, but was: $number" }
            return PokemonNumber(number.toString())
        }

        fun unknown(): PokemonNumber = PokemonNumber("0")
    }
}
