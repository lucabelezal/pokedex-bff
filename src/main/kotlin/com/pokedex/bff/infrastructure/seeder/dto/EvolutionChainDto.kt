package com.pokedex.bff.infrastructure.seeder.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EvolutionChainDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("chain")
    val chainData: Any
)
