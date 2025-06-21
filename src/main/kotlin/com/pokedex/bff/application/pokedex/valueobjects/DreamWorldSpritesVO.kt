package com.pokedex.bff.application.pokedex.valueobjects

import com.fasterxml.jackson.annotation.JsonProperty

data class DreamWorldSpritesVO(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null
)
