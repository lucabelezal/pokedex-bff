package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EvolutionChainDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("chain")
    val chainData: Any
)
