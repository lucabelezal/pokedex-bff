package com.pokedex.bff.application.pokedex.valueobjects

import com.fasterxml.jackson.annotation.JsonProperty

data class OtherSpritesVO(
    @JsonProperty("dream_world")
    val dreamWorld: DreamWorldSpritesVO? = null,
    @JsonProperty("home")
    val home: HomeSpritesVO? = null,
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkSpritesVO? = null,
    @JsonProperty("showdown")
    val showdown: ShowdownSpritesVO? = null
)