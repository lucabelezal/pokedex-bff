package com.pokedex.bff.controllers.dtos

data class Pokemon(
    val number: String,
    val name: String,
    val image: PokemonImage,
    val types: List<PokemonType>
)
