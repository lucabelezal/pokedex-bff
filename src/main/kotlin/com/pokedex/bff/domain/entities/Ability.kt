package com.pokedex.bff.domain.entities

data class Ability(
    val id: Long,
    val name: String,
    val description: String?,
    val introducedGeneration: Generation?
)
