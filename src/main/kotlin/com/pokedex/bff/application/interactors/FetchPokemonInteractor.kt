package com.pokedex.bff.application.interactors

import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.application.usecases.pokedex.FetchPokemonUseCase
import com.pokedex.bff.domain.entities.Pokemon
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FetchPokemonInteractor(
    private val pokemonRepository: PokemonRepository
) : FetchPokemonUseCase {
    @Transactional(readOnly = true)
    override fun execute(id: Long): Pokemon? {
        require(id > 0) { "Pokemon ID must be positive" }
        return pokemonRepository.findById(id)
    }
}

