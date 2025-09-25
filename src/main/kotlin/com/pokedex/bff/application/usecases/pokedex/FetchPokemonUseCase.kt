package com.pokedex.bff.application.usecases.pokedex

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.repositories.PokemonRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Use case for retrieving a single Pokemon by its ID
 * 
 * This use case implements the business logic for fetching individual Pokemon,
 * following Clean Architecture principles by depending only on domain interfaces.
 */
@Component
class FetchPokemonUseCase(
    private val pokemonRepository: PokemonRepository
) {
    
    /**
     * Executes the use case to get a Pokemon by ID
     * 
     * @param id the Pokemon ID
     * @return the Pokemon or null if not found
     */
    @Transactional(readOnly = true)
    fun execute(id: Long): Pokemon? {
        require(id > 0) { "Pokemon ID must be positive" }
        return pokemonRepository.findById(id)
    }
}

