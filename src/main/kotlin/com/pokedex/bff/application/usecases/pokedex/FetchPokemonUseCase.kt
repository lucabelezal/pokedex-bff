package com.pokedex.bff.application.usecases.pokedex

import com.pokedex.bff.domain.entities.Pokemon

/**
 * Use case interface for retrieving a single Pokemon by its ID
 */
interface FetchPokemonUseCase {
    /**
     * Executes the use case to get a Pokemon by ID
     * @param id the Pokemon ID
     * @return the Pokemon or null if not found
     */
    fun execute(id: Long): Pokemon?
}

