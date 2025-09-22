package com.pokedex.bff.interfaces.dto

import com.pokedex.bff.domain.entities.Pokemon

data class PokemonDto(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(pokemon: Pokemon) = PokemonDto(
            id = pokemon.id,
            name = pokemon.name
        )
    }
}
