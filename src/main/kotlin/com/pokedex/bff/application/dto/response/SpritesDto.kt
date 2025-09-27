package com.pokedex.bff.application.dto.response

data class SpritesDto(
    val backDefault: String? = null,
    val backFemale: String? = null,
    val backShiny: String? = null,
    val backShinyFemale: String? = null,
    val frontDefault: String? = null,
    val frontFemale: String? = null,
    val frontShiny: String? = null,
    val frontShinyFemale: String? = null,
    val other: OtherSpritesDto? = null
)

