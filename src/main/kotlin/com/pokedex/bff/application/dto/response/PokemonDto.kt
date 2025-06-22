package com.pokedex.bff.application.dto.response

data class PokemonDto(
    val number: String,
    val name: String,
    val image: PokemonImageDto,
    val types: List<PokemonTypeDto>
)
