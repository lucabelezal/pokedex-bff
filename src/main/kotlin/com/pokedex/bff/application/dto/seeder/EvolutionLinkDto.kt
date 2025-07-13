package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class EvolutionLinkDto(
    @JsonProperty("species_name")
    val speciesName: String,
    @JsonProperty("evolves_to")
    val evolvesTo: List<EvolutionLinkDto>
)
