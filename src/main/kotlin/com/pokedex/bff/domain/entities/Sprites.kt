package com.pokedex.bff.domain.entities

data class Sprites(
    val backDefault: String?,
    val backFemale: String?,
    val backShiny: String?,
    val backShinyFemale: String?,
    val frontDefault: String?,
    val frontFemale: String?,
    val frontShiny: String?,
    val frontShinyFemale: String?,
    val other: OtherSprites?
)

data class OtherSprites(
    val dreamWorld: String? = null,
    val home: String? = null,
    val officialArtwork: String? = null,
    val showdown: String? = null
)

// Outras classes auxiliares (OtherSprites, etc.) devem ser migradas conforme necessidade.
