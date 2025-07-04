package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class GenderRateDTO(
    @JsonProperty("male")
    val male: Float,
    @JsonProperty("female")
    val female: Float
)
