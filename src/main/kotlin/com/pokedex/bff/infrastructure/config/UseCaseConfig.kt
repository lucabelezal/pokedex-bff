package com.pokedex.bff.infrastructure.config

import com.pokedex.bff.application.usecase.FetchPokemonUseCase
import com.pokedex.bff.domain.repositories.PokemonRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig {
    @Bean
    fun fetchPokemonUseCase(pokemonRepository: PokemonRepository) =
        FetchPokemonUseCase(pokemonRepository)
}

