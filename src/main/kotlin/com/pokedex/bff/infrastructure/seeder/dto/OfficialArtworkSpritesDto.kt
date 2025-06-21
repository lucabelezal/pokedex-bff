package com.pokedex.bff.infrastructure.seeder.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.application.pokedex.valueobjects.OfficialArtworkSpritesVO

data class OfficialArtworkSpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?
) {
    fun toVO(): OfficialArtworkSpritesVO {
        return OfficialArtworkSpritesVO(
            frontDefault = this.frontDefault,
            frontShiny = this.frontShiny
        )
    }
}