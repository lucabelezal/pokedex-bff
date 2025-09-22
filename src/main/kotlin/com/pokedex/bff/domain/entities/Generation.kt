package com.pokedex.bff.domain.entities

data class Generation(
    val id: Long,
    val name: String,
    val region: Region?
)
