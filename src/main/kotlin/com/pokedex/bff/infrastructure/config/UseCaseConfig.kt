
package com.pokedex.bff.infrastructure.config

import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.application.usecases.pokedex.FetchPokemonUseCase
import com.pokedex.bff.application.usecases.pokedex.GetPaginatedPokemonsUseCase
import com.pokedex.bff.application.interactors.FetchPokemonInteractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for application use cases
 *
 * This configuration follows Clean Architecture principles by keeping
 * the application layer independent of frameworks and infrastructure details.
 * Use cases are configured here to ensure proper dependency injection.
 */
@Configuration
class UseCaseConfig {
    @Bean

    fun fetchPokemonUseCase(pokemonRepository: PokemonRepository): FetchPokemonUseCase =
        FetchPokemonInteractor(pokemonRepository)

    @Bean

    fun getPaginatedPokemonsUseCase(pokemonRepository: PokemonRepository): GetPaginatedPokemonsUseCase =
        GetPaginatedPokemonsUseCase(pokemonRepository)
}
