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
        in GENERATION_1_RANGE -> 1
        in GENERATION_2_RANGE -> 2
        in GENERATION_3_RANGE -> 3
        in GENERATION_4_RANGE -> 4
        in GENERATION_5_RANGE -> 5
        in GENERATION_6_RANGE -> 6
        in GENERATION_7_RANGE -> 7
        in GENERATION_8_RANGE -> 8
        in GENERATION_9_RANGE -> 9
        else -> 0 // Unknown generation
    }
    
    companion object {
        const val MAX_POKEMON_ID = 1010L // Current maximum Pokemon ID
        
        private val GENERATION_1_RANGE = 1L..151L
        private val GENERATION_2_RANGE = 152L..251L
        private val GENERATION_3_RANGE = 252L..386L
        private val GENERATION_4_RANGE = 387L..493L
        private val GENERATION_5_RANGE = 494L..649L
        private val GENERATION_6_RANGE = 650L..721L
        private val GENERATION_7_RANGE = 722L..809L
        private val GENERATION_8_RANGE = 810L..905L
        private val GENERATION_9_RANGE = 906L..1010L
    }
}