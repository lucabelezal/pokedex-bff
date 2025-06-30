package com.pokedex.domain.dto

import com.pokedex.domain.entity.PokemonType

data class PokemonDTO(
    val id: String,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonType>,
    val stats: List<PokemonStatDTO>,
    val gender: PokemonGenderDTO
)
