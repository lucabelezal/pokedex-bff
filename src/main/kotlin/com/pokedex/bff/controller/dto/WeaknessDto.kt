package com.pokedex.bff.controller.dto

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