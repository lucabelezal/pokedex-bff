package com.pokedex.bff.application.dtos.output

data class BattleResultOutput(
    val winnerId: String,
    val loserId: String,
    val summary: String
)
