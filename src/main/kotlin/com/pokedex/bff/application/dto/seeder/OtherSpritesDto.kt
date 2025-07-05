package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.application.valueobjects.OtherSpritesVO

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
    fun toVO(): OtherSpritesVO {
        return OtherSpritesVO(
            dreamWorld = this.dreamWorld?.toVO(),
            home = this.home?.toVO(),
            officialArtwork = this.officialArtwork?.toVO(),
            showdown = this.showdown?.toVO()
        )
    }
}