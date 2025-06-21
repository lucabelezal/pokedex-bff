package com.pokedex.bff.infrastructure.seeder.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EggGroupDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String
)