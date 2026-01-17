package com.pokedex.bff.application.dtos.output

import com.pokedex.bff.domain.pokemon.entities.Pokemon

data class PokemonOutput(
    val id: String,
    val name: String,
    val types: List<String>
) {
    companion object {
        fun fromDomain(pokemon: Pokemon): PokemonOutput =
            PokemonOutput(
                id = pokemon.id.toString(),
                name = pokemon.name,
                types = pokemon.types.map { it.name }
            )
    }
}
