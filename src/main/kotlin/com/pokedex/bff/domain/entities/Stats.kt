package com.pokedex.bff.domain.entities

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
