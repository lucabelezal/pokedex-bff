package com.pokedex.bff.application.pokedex.dto.response

data class PokemonDto(
    val number: String,
    val name: String,
    val image: PokemonImageDto,
    val types: List<PokemonTypeDto>
)
