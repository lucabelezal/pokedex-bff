package com.pokedex.bff.infrastructure.config

import com.pokedex.bff.application.usecase.BuscarPokemonUseCase
import com.pokedex.bff.domain.repository.PokemonRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig {
    @Bean
    fun buscarPokemonUseCase(pokemonRepository: PokemonRepository) =
        BuscarPokemonUseCase(pokemonRepository)
}

