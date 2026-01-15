package com.pokedex.bff.adapters.input.web.dto.response

data class PokemonPageResponse(
    val pokemons: List<PokemonWebResponse>,
    val totalElements: Long,
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean
)

