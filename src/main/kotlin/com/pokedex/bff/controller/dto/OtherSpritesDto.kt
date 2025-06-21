package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.infra.entity.OtherSprites

data class OtherSpritesDto(
    @JsonProperty("dream_world")
    val dreamWorld: DreamWorldSpritesDto?,
    @JsonProperty("home")
    val home: HomeSpritesDto?,
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkSpritesDto?,
    @JsonProperty("showdown")
    val showdown: ShowdownSpritesDto?
) {
    fun toModel(): OtherSprites {
        return OtherSprites(
            dreamWorld = this.dreamWorld?.toModel(),
            home = this.home?.toModel(),
            officialArtwork = this.officialArtwork?.toModel(),
            showdown = this.showdown?.toModel()
        )
    }
}