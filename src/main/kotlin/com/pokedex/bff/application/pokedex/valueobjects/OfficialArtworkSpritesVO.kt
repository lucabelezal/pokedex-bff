package com.pokedex.bff.application.pokedex.valueobjects

import com.fasterxml.jackson.annotation.JsonProperty

data class OfficialArtworkSpritesVO(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_shiny")
    val frontShiny: String? = null
)