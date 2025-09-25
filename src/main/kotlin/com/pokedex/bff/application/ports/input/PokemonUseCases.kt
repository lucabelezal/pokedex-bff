package com.pokedex.bff.application.ports.input

import com.pokedex.bff.application.dto.response.PokedexListResponse
import com.pokedex.bff.domain.entities.Pokemon

/**
 * Port for Pokemon-related use cases
 * Defines the contract for all Pokemon operations including individual fetch and paginated lists
 */
interface PokemonUseCases {
    
    /**
     * Fetches a single Pokemon by its ID
     * 
     * @param id the Pokemon ID
     * @return the Pokemon or null if not found
     */
    fun findById(id: Long): Pokemon?
    
    /**
     * Fetches a paginated list of Pokemon for the Pokedex
     * 
     * @param page the page number (zero-based)
     * @param size the number of Pokemon per page
     * @return paginated response with Pokemon list and pagination info
     */
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
}