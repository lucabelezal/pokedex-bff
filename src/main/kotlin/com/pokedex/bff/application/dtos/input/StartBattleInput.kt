package com.pokedex.bff.application.dtos.input

data class StartBattleInput(
    val pokemonId: String,
    val opponentId: String
)
