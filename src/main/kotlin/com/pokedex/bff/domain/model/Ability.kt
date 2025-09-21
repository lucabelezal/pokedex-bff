package com.pokedex.bff.domain.model

data class Ability(
    val id: Long,
    val name: String,
    val description: String?,
    val introducedGeneration: Generation?
)
