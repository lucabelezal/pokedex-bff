package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class GenerationDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("region_id")
    val regionId: Long
)