package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.infra.entity.Sprites

data class SpritesDto(
    @JsonProperty("back_default")
    val backDefault: String?,
    @JsonProperty("back_female")
    val backFemale: String?,
    @JsonProperty("back_shiny")
    val backShiny: String?,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String?,
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?,
    @JsonProperty("other")
    val other: OtherSpritesDto?
) {
    fun toModel(): Sprites {
        return Sprites(
            backDefault = this.backDefault,
            backFemale = this.backFemale,
            backShiny = this.backShiny,
            backShinyFemale = this.backShinyFemale,
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale,
            other = this.other?.toModel()
        )
    }
}