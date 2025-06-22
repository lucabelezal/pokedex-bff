package com.pokedex.bff.application.dto.seeder

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.application.valueobjects.ShowdownSpritesVO

data class ShowdownSpritesDto(
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
    val frontShinyFemale: String?
) {
    fun toVO(): ShowdownSpritesVO {
        return ShowdownSpritesVO(
            backDefault = this.backDefault,
            backFemale = this.backFemale,
            backShiny = this.backShiny,
            backShinyFemale = this.backShinyFemale,
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale
        )
    }
}