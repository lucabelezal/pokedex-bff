package com.pokedex.bff.domain.model

data class Generation(
    val id: Long,
    val name: String,
    val region: Region?
)
