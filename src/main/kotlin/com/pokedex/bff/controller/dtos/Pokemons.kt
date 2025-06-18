package com.pokedex.bff.controller.dtos

data class PokemonListResponse(
    val pageInfo: PageInfo,
    val search: Search,
    val filters: List<Any>,
    val pokemons: List<PokemonDetail>
)

data class PageInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean
)

data class Search(
    val placeholder: String
)

data class PokemonDetail(
    val number: String,
    val name: String,
    val image: PokemonImage,
    val types: List<PokemonType>
)

data class PokemonImage(
    val url: String,
    val element: PokemonImageElement
)

data class PokemonImageElement(
    val color: String,
    val type: String
)

data class PokemonType(
    val name: String,
    val color: String
)
