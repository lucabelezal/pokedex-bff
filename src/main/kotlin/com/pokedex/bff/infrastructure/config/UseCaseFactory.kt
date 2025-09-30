package com.pokedex.bff.infrastructure.config

import com.pokedex.bff.application.interactor.CreatePokemonInteractor
import com.pokedex.bff.application.interactor.EvolvePokemonInteractor
import com.pokedex.bff.application.interactor.BattleInteractor
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import com.pokedex.bff.domain.pokemon.service.PokemonEvolutionService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseFactory {
    @Bean
    fun createPokemonInteractor(pokemonRepository: PokemonRepository) =
        CreatePokemonInteractor(pokemonRepository)

    @Bean
    fun evolvePokemonInteractor(pokemonRepository: PokemonRepository, evolutionService: PokemonEvolutionService) =
        EvolvePokemonInteractor(pokemonRepository, evolutionService)

    @Bean
    fun battleInteractor() = BattleInteractor()
}
