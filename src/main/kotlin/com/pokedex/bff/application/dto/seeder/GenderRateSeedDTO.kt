package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty

data class GenderRateSeedDTO(
    @JsonProperty("male")
    val male: Float,
    @JsonProperty("female")
    val female: Float
)
