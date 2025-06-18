package com.pokedex.bff.controllers.dtos

data class PokemonHomeResponse(
    val search: Search,
    val filters: List<Filter>,
    val pokemons: List<Pokemon>
)