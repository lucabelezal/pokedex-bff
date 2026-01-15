package com.pokedex.bff.adapters.input.web.dto.response

// Estrutura para o frontend

data class PokemonTypeWebResponse(
    val name: String,
    val color: String
)

data class PokemonImageElementWebResponse(
    val color: String,
    val type: String
)

data class PokemonImageWebResponse(
    val url: String,
    val element: PokemonImageElementWebResponse
)

data class PokemonRichWebResponse(
    val number: String,
    val name: String,
    val image: PokemonImageWebResponse,
    val types: List<PokemonTypeWebResponse>
)

data class PokemonRichPageResponse(
    val search: SearchWebResponse,
    val filters: List<Any>,
    val pokemons: List<PokemonRichWebResponse>
)

data class SearchWebResponse(
    val placeholder: String
)
