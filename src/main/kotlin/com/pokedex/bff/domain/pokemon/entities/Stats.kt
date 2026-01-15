package com.pokedex.bff.domain.pokemon.entities

data class Stats(
    val id: Long,
    val total: Int,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int
)