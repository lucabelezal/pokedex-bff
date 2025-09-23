package com.pokedex.bff.application.ports.input

import com.pokedex.bff.application.dto.response.PokedexListResponse

/**
 * Port for Pokedex-related use cases
 * Defines the contract for fetching Pokemon lists with pagination
 */
interface PokedexUseCases {
    
    /**
     * Fetches a paginated list of Pokemon for the Pokedex
     * 
     * @param page the page number (zero-based)
     * @param size the number of Pokemon per page
     * @return paginated response with Pokemon list and pagination info
     */
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
}