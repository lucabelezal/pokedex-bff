package com.pokedex.bff.application.pokedex.dto.response

data class PokedexListResponse(
    val pageInfo: PageInfoDto,
    val search: SearchDto,
    val filters: List<Any>,
    val pokemons: List<PokemonDto>
)