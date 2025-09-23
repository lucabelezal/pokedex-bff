package com.pokedex.bff.application.usecase

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.repositories.PokemonRepository

class FetchPokemonUseCase(
    private val pokemonRepository: PokemonRepository
) {
    fun execute(id: Long): Pokemon? = pokemonRepository.findById(id)
}

