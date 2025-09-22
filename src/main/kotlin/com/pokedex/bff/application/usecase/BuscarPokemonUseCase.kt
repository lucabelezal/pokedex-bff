package com.pokedex.bff.application.usecase

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.repository.PokemonRepository

class BuscarPokemonUseCase(
    private val pokemonRepository: PokemonRepository
) {
    fun execute(id: Long): Pokemon? = pokemonRepository.findById(id)
}

