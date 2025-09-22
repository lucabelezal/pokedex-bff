package com.pokedex.bff.domain.valueobjects

import com.fasterxml.jackson.annotation.JsonProperty

data class DreamWorldSpritesVO(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null
)
