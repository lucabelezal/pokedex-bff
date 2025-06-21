package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.infra.entity.HomeSprites

data class HomeSpritesDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?
) {
    fun toModel(): HomeSprites {
        return HomeSprites(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale
        )
    }
}