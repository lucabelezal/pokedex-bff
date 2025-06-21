package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.infra.entity.OfficialArtworkSprites

data class OfficialArtworkSpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?
) {
    fun toModel(): OfficialArtworkSprites {
        return OfficialArtworkSprites(
            frontDefault = this.frontDefault,
            frontShiny = this.frontShiny
        )
    }
}