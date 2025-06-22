package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class EggGroupDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String
)