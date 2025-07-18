package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class AbilityDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("introduced_generation_id")
    val introducedGenerationId: Long
)