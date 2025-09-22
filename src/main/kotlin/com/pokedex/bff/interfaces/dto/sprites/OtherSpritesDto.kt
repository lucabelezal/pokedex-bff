package com.pokedex.bff.interfaces.dto.sprites

import com.fasterxml.jackson.annotation.JsonProperty

data class OtherSpritesDto(
    @JsonProperty("dream_world")
    val dreamWorld: DreamWorldSpritesDto? = null,
    @JsonProperty("home")
    val home: HomeSpritesDto? = null,
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkSpritesDto? = null,
    @JsonProperty("showdown")
    val showdown: ShowdownSpritesDto? = null
)