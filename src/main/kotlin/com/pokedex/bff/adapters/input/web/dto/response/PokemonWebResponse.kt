package com.pokedex.bff.adapters.input.web.dto.response

data class PokemonWebResponse(
    val id: String,
    val name: String,
    val types: List<String>
)
