package com.pokedex.bff.interfaces.dto.sprites

import com.fasterxml.jackson.annotation.JsonProperty

data class DreamWorldSpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null
)
