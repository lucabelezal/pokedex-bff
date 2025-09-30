package com.pokedex.bff.domain.pokemon.entities

data class Generation(
    val id: Long,
    val name: String,
    val region: Region?
)