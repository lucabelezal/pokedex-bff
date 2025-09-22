package com.pokedex.bff.interfaces.dto.sprites

import com.fasterxml.jackson.annotation.JsonProperty

data class OfficialArtworkSpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_shiny")
    val frontShiny: String? = null
)