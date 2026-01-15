package com.pokedex.bff.domain.pokemon.entities

data class Ability(
    val id: Long,
    val name: String,
    val description: String?,
    val introducedGeneration: Generation?
)