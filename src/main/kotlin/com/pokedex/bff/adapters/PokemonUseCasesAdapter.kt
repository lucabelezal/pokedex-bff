package com.pokedex.bff.adapters

import com.pokedex.bff.application.dto.response.PokedexListResponse
import com.pokedex.bff.application.ports.input.PokemonUseCases
import com.pokedex.bff.application.usecases.pokedex.FetchPokemonUseCase
import com.pokedex.bff.application.usecases.pokedex.GetPaginatedPokemonsUseCase
import com.pokedex.bff.domain.entities.Pokemon
import org.springframework.stereotype.Service

/**
 * Adapter that implements the PokemonUseCases port
 *
 * This adapter acts as a bridge between the interface layer and the application layer,
 * delegating to specific use cases while maintaining the contract defined by the port.
 * Following Clean Architecture principles, this adapter isolates the application core
 * from framework details.
 */
@Service
class PokemonUseCasesAdapter(
    private val fetchPokemonUseCase: FetchPokemonUseCase,
    private val getPaginatedPokemonsUseCase: GetPaginatedPokemonsUseCase
) : PokemonUseCases {

    override fun findById(id: Long): Pokemon? {
        return fetchPokemonUseCase.execute(id)
    }

    override fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse {
        return getPaginatedPokemonsUseCase.execute(page, size)
    }
}
