package com.pokedex.bff.domain.entities

data class Species(
    val id: Long,
    val pokemonNumber: String?,
    val name: String,
    val speciesEn: String?,
    val speciesPt: String?
)
