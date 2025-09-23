package com.pokedex.bff.infrastructure.adapters

import com.pokedex.bff.application.dto.response.PokedexListResponse
import com.pokedex.bff.application.ports.input.PokedexUseCases
import com.pokedex.bff.application.usecases.pokedex.GetPaginatedPokemonsUseCase
import org.springframework.stereotype.Service

/**
 * Infrastructure adapter that implements the PokedexUseCases port
 * 
 * This adapter acts as a bridge between the interface layer and the application layer,
 * delegating to specific use cases while maintaining the contract defined by the port.
 */
@Service
class PokedexUseCasesAdapter(
    private val getPaginatedPokemonsUseCase: GetPaginatedPokemonsUseCase
) : PokedexUseCases {

    override fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse {
        return getPaginatedPokemonsUseCase.execute(page, size)
    }
}