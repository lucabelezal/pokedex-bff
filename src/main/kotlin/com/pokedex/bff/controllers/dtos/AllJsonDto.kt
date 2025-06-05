package com.pokedex.bff.controllers.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class PokemonJsonDto(
    val id: Int,
    val nationalPokedexNumber: String,
    val name: String,
    val statsId: Int?, // Será usado para buscar o Stats
    val generationId: Int, // Será usado para buscar a Generation
    val speciesId: Int, // Será usado para buscar a Species
    val heightM: BigDecimal?,
    val weightKg: BigDecimal?,
    val description: String?,
    val sprites: Map<String, Any>?, // JSONB como Map<String, Any> ou String pura
    @JsonProperty("gender_rate_value")
    val genderRateValue: Int?,
    @JsonProperty("eggCycles")
    val eggCycles: Int?,
    val typeId: List<Int>?, // Lista de IDs de Tipos
    val abilities: List<PokemonAbilityJsonDto>?, // Lista de habilidades com isHidden
    val eggGroupIds: List<Int>?, // Lista de IDs de EggGroups
    @JsonProperty("type_matchup")
    val typeMatchup: Map<String, Any>? // Não mapearemos isso para o DB, apenas para leitura se precisar
)

data class PokemonAbilityJsonDto(
    val abilityId: String, // O nome da habilidade é o ID
    val isHidden: Boolean?
)

data class WeaknessJsonDto(
    val id: Int?, // Não é o ID da tabela de junção, mas do item do JSON
    @JsonProperty("pokemon_id")
    val pokemonId: Int,
    @JsonProperty("pokemon_name")
    val pokemonName: String?,
    val weaknesses: List<String> // Lista de nomes de tipos
)

data class EvolutionChainJsonDto(
    val id: Int,
    val chain: List<EvolutionLinkJsonDto>
)

data class EvolutionLinkJsonDto(
    @JsonProperty("pokemonId")
    val pokemonId: Int,
    @JsonProperty("pokemonName")
    val pokemonName: String?,
    @JsonProperty("evolutionDetails")
    val evolutionDetails: List<EvolutionDetailJsonDto>
)

data class EvolutionDetailJsonDto(
    @JsonProperty("targetPokemonId")
    val targetPokemonId: Int?,
    @JsonProperty("targetPokemonName")
    val targetPokemonName: String?,
    val condition: EvolutionConditionJsonDto?
)

data class EvolutionConditionJsonDto(
    val type: String?,
    val value: String? // Pode ser nível, item, etc.
)