package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class SpeciesDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("pokemon_number")
    val pokemonNumber: String?,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("species_en")
    val speciesEn: String?,
    @JsonProperty("species_pt")
    val speciesPt: String?
)