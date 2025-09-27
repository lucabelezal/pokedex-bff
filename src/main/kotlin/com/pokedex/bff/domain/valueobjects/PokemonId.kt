package com.pokedex.bff.domain.valueobjects

/**
 * Value Object representing a Pokemon's unique identifier
 *
 * Ensures that Pokemon IDs are always positive and valid according to business rules.
 * This is a pure domain concept with no infrastructure concerns.
 */
@JvmInline
value class PokemonId(val value: Long) {
    
    init {
        require(value > 0) { "Pokemon ID must be positive, but was: $value" }
        require(value <= MAX_POKEMON_ID) { "Pokemon ID cannot exceed $MAX_POKEMON_ID, but was: $value" }
    }
    
    /**
     * Checks if this Pokemon ID represents a valid Generation I Pokemon
     */
    fun isGeneration1(): Boolean = value in GENERATION_1_RANGE
    
    /**
     * Checks if this Pokemon ID represents a valid Generation II Pokemon
     */
    fun isGeneration2(): Boolean = value in GENERATION_2_RANGE
    
    /**
     * Gets the generation number for this Pokemon ID
     */
    fun getGeneration(): Int = when (value) {
        in GENERATION_1_RANGE -> GENERATION_1
        in GENERATION_2_RANGE -> GENERATION_2
        in GENERATION_3_RANGE -> GENERATION_3
        in GENERATION_4_RANGE -> GENERATION_4
        in GENERATION_5_RANGE -> GENERATION_5
        in GENERATION_6_RANGE -> GENERATION_6
        in GENERATION_7_RANGE -> GENERATION_7
        in GENERATION_8_RANGE -> GENERATION_8
        in GENERATION_9_RANGE -> GENERATION_9
        else -> 0 // Unknown generation
    }
    
    companion object {
        const val MAX_POKEMON_ID = 1010L // Current maximum Pokemon ID

        // Generation boundaries
        private const val G1_START = 1L
        private const val G1_END = 151L
        private const val G2_START = 152L
        private const val G2_END = 251L
        private const val G3_START = 252L
        private const val G3_END = 386L
        private const val G4_START = 387L
        private const val G4_END = 493L
        private const val G5_START = 494L
        private const val G5_END = 649L
        private const val G6_START = 650L
        private const val G6_END = 721L
        private const val G7_START = 722L
        private const val G7_END = 809L
        private const val G8_START = 810L
        private const val G8_END = 905L
        private const val G9_START = 906L
        private const val G9_END = 1010L

        private val GENERATION_1_RANGE = G1_START..G1_END
        private val GENERATION_2_RANGE = G2_START..G2_END
        private val GENERATION_3_RANGE = G3_START..G3_END
        private val GENERATION_4_RANGE = G4_START..G4_END
        private val GENERATION_5_RANGE = G5_START..G5_END
        private val GENERATION_6_RANGE = G6_START..G6_END
        private val GENERATION_7_RANGE = G7_START..G7_END
        private val GENERATION_8_RANGE = G8_START..G8_END
        private val GENERATION_9_RANGE = G9_START..G9_END

        // Generation identifiers
        private const val GENERATION_1 = 1
        private const val GENERATION_2 = 2
        private const val GENERATION_3 = 3
        private const val GENERATION_4 = 4
        private const val GENERATION_5 = 5
        private const val GENERATION_6 = 6
        private const val GENERATION_7 = 7
        private const val GENERATION_8 = 8
        private const val GENERATION_9 = 9
    }
}


