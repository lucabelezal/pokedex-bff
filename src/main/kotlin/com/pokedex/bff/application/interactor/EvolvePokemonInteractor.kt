package com.pokedex.bff.application.interactor

import com.pokedex.bff.application.dtos.input.EvolvePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput
import com.pokedex.bff.application.usecase.EvolvePokemonUseCase
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import com.pokedex.bff.domain.pokemon.service.PokemonEvolutionService

class EvolvePokemonInteractor(
    private val pokemonRepository: PokemonRepository,
    private val evolutionService: PokemonEvolutionService
) : EvolvePokemonUseCase {
    override fun execute(input: EvolvePokemonInput): PokemonOutput {
        // TODO: Implementar lógica de evolução de Pokémon
        // Exemplo fictício:
        val pokemon = pokemonRepository.findById(input.pokemonId)
            ?: throw Exception("Pokémon não encontrado")
        val evolved = evolutionService.evolve(pokemon)
        pokemonRepository.save(evolved)
        return PokemonOutput.fromDomain(evolved)
    }
}
