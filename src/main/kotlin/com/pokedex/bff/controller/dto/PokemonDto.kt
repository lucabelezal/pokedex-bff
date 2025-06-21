package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("number")
    val number: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("height")
    val height: Double,
    @JsonProperty("weight")
    val weight: Double,
    @JsonProperty("stats_id")
    val statsId: Long,
    @JsonProperty("generation_id")
    val generationId: Long,
    @JsonProperty("species_id")
    val speciesId: Long,
    @JsonProperty("region_id")
    val regionId: Long?,
    @JsonProperty("evolution_chain_id")
    val evolutionChainId: Long,
    @JsonProperty("gender_rate_value")
    val genderRateValue: Int,
    @JsonProperty("egg_cycles")
    val eggCycles: Int?,
    @JsonProperty("egg_group_ids")
    val eggGroupIds: List<Long>,
    @JsonProperty("type_ids")
    val typeIds: List<Long>,
    @JsonProperty("abilities")
    val abilities: List<PokemonAbilityDto>,
    @JsonProperty("sprites")
    val sprites: SpritesDto
)