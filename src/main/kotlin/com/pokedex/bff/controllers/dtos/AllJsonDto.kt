package com.pokedex.bff.controllers.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal // Keep BigDecimal if needed elsewhere, but Double matches the JSON for type_matchup

// DTO for the main Pokemon data structure (from 8_pokemon.json)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class) // Use SnakeCaseStrategy for automatic mapping
data class PokemonJsonDto(
    val id: Int,
    val nationalPokedexNumber: String, // national_pokedex_number
    val name: String,
    val statsId: Int?,
    val generationId: Int, // generation_id
    val speciesId: Int, // species_id
    val heightM: BigDecimal?, // height_m (BigDecimal is good for decimals)
    val weightKg: BigDecimal?, // weight_kg (BigDecimal is good for decimals)
    val description: String?,
    val sprites: SpritesJsonDto?, // Use the dedicated Sprites DTO
    val genderRateValue: Int?, // gender_rate_value
    val eggCycles: Int?, // egg_cycles
    val typeId: List<Int>?, // type_id (already camelCase in your JSON sample)
    val abilities: List<PokemonAbilityJsonDto>?, // abilities (already camelCase in your JSON sample)
    val eggGroupIds: List<Int>?, // egg_group_ids (already camelCase in your JSON sample)
    val typeMatchup: Map<String, Map<String, Double>>? // type_matchup (values are Doubles in JSON sample)
)

// DTO for the 'abilities' array items (from 8_pokemon.json)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PokemonAbilityJsonDto(
    val abilityId: Int, // ability_id (already camelCase in your JSON sample)
    val isHidden: Boolean? // is_hidden (already camelCase in your JSON sample)
)

// DTO for the 'sprites' object (nested in 8_pokemon.json)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SpritesJsonDto(
    val backDefault: String?, // back_default
    val backFemale: String?, // back_female
    val backShiny: String?, // back_shiny
    val backShinyFemale: String?, // back_shiny_female
    val frontDefault: String?, // front_default
    val frontFemale: String?, // front_female
    val frontShiny: String?, // front_shiny
    val frontShinyFemale: String?, // front_shiny_female
    val other: OtherSpritesJsonDto? // other
)

// DTO for the 'other' sprites object (nested in sprites)
@JsonIgnoreProperties(ignoreUnknown = true)
// Removed @JsonNaming here as it doesn't handle hyphens and was confusing the mapping
data class OtherSpritesJsonDto(
    val dreamWorld: DreamWorldSpritesJsonDto?, // dream_world - handled by SnakeCaseStrategy from parent or explicit @JsonProperty
    val home: HomeSpritesJsonDto?, // home - handled by SnakeCaseStrategy from parent or explicit @JsonProperty
    // Use @JsonProperty directly on the parameter to map "official-artwork"
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkSpritesJsonDto?, // official-artwork
    val showdown: ShowdownSpritesJsonDto? // showdown - handled by SnakeCaseStrategy from parent or explicit @JsonProperty
) {
    // Removed the conflicting 'var officialArtwork' and getter/setter functions
    // The @JsonProperty on the constructor parameter is sufficient.
}

// DTO for 'dream_world' sprites
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DreamWorldSpritesJsonDto(
    val frontDefault: String?, // front_default
    val frontFemale: String? // front_female
)

// DTO for 'home' sprites
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class HomeSpritesJsonDto(
    val frontDefault: String?, // front_default
    val frontFemale: String?, // front_female
    val frontShiny: String?, // front_shiny
    val frontShinyFemale: String? // front_shiny_female
)

// DTO for 'official-artwork' sprites
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class) // This strategy *does* apply to front_default/front_shiny
data class OfficialArtworkSpritesJsonDto(
    val frontDefault: String?, // front_default
    val frontShiny: String? // front_shiny
)


// DTO for 'showdown' sprites
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ShowdownSpritesJsonDto(
    val backDefault: String?, // back_default
    val backFemale: String?, // back_female
    val backShiny: String?, // back_shiny
    val backShinyFemale: String?, // back_shiny_female
    val frontDefault: String?, // front_default
    val frontFemale: String?, // front_female
    val frontShiny: String?, // front_shiny
    val frontShinyFemale: String? // front_shiny_female
)


// DTO for the Weakness data structure (from weaknesses.json)
@JsonIgnoreProperties(ignoreUnknown = true)
// No NamingStrategy needed here as fields already match (except @JsonProperty used below)
data class WeaknessJsonDto(
    val id: Int?, // Optional ID from weaknesses.json
    @JsonProperty("pokemon_id") // Explicit mapping for snake_case
    val pokemonId: Int, // References Pokemon.id
    @JsonProperty("pokemon_name") // Explicit mapping for snake_case
    val pokemonName: String?,
    val weaknesses: List<String>
)

// DTO for the top-level Evolution Chain data structure (from evolution_chains.json)
@JsonIgnoreProperties(ignoreUnknown = true)
// No NamingStrategy needed here
data class EvolutionChainJsonDto(
    val id: Int,
    val chain: List<EvolutionLinkJsonDto>
)

// DTO for items within the 'chain' array
@JsonIgnoreProperties(ignoreUnknown = true)
// No NamingStrategy needed here
data class EvolutionLinkJsonDto(
    @JsonProperty("pokemonId") // Explicit mapping for camelCase in JSON
    val pokemonId: Int,
    @JsonProperty("pokemonName") // Explicit mapping for camelCase in JSON
    val pokemonName: String?,
    @JsonProperty("evolutionDetails") // Explicit mapping for camelCase in JSON
    val evolutionDetails: List<EvolutionDetailJsonDto>
)

// DTO for items within the 'evolutionDetails' array
@JsonIgnoreProperties(ignoreUnknown = true)
// No NamingStrategy needed here
data class EvolutionDetailJsonDto(
    @JsonProperty("targetPokemonId") // Explicit mapping for camelCase in JSON
    val targetPokemonId: Int?,
    @JsonProperty("targetPokemonName") // Explicit mapping for camelCase in JSON
    val targetPokemonName: String?,
    val condition: EvolutionConditionJsonDto?
)

// DTO for the 'condition' object
@JsonIgnoreProperties(ignoreUnknown = true)
// No NamingStrategy needed here
data class EvolutionConditionJsonDto(
    val type: String?,
    val value: String?
)