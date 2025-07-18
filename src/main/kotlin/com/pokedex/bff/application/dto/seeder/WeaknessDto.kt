package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class WeaknessDto(
    @JsonProperty("id")
    val id: Long?,
    @JsonProperty("pokemon_id")
    val pokemonId: Long?,
    @JsonProperty("pokemon_name")
    val pokemonName: String,
    @JsonProperty("weaknesses")
    val weaknesses: List<String>
)