package com.pokedex.bff.infrastructure.config

import com.pokedex.bff.application.port.input.CreatePokemonUseCase
import com.pokedex.bff.application.port.input.EvolvePokemonUseCase
import com.pokedex.bff.application.port.input.BattleUseCase
import com.pokedex.bff.application.port.input.ListPokemonsUseCase
import com.pokedex.bff.application.usecase.CreatePokemonUseCaseImpl
import com.pokedex.bff.application.usecase.EvolvePokemonUseCaseImpl
import com.pokedex.bff.application.usecase.BattleUseCaseImpl
import com.pokedex.bff.application.usecase.ListPokemonsUseCaseImpl
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseFactory {

    @Bean
    fun createPokemonUseCase(pokemonRepository: PokemonRepository): CreatePokemonUseCase =
        CreatePokemonUseCaseImpl(pokemonRepository)

    @Bean
    fun listPokemonsUseCase(pokemonRepository: PokemonRepository): ListPokemonsUseCase =
        ListPokemonsUseCaseImpl(pokemonRepository)

    @Bean
    fun evolvePokemonUseCase(pokemonRepository: PokemonRepository): EvolvePokemonUseCase =
        EvolvePokemonUseCaseImpl(pokemonRepository)

    @Bean
    fun battleUseCase(pokemonRepository: PokemonRepository): BattleUseCase =
        BattleUseCaseImpl(pokemonRepository)
}
