package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.application.valueobjects.HomeSpritesVO

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
    fun toVO(): HomeSpritesVO {
        return HomeSpritesVO(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale
        )
    }
}