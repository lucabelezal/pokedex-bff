package com.pokedex.bff.application.pokedex.valueobjects

import com.fasterxml.jackson.annotation.JsonProperty

data class SpritesVO(
    @JsonProperty("back_default")
    val backDefault: String? = null,
    @JsonProperty("back_female")
    val backFemale: String? = null,
    @JsonProperty("back_shiny")
    val backShiny: String? = null,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String? = null,
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null,
    @JsonProperty("front_shiny")
    val frontShiny: String? = null,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String? = null,
    @JsonProperty("other")
    val other: OtherSpritesVO? = null
)