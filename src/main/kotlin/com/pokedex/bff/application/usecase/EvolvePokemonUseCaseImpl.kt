package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.port.input.EvolvePokemonUseCase
import com.pokedex.bff.application.dtos.input.EvolvePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import com.pokedex.bff.domain.pokemon.exception.PokemonNotFoundException
import com.pokedex.bff.domain.pokemon.exception.InvalidPokemonException
import org.slf4j.LoggerFactory

class EvolvePokemonUseCaseImpl(
    private val pokemonRepository: PokemonRepository
) : EvolvePokemonUseCase {
    
    private val logger = LoggerFactory.getLogger(EvolvePokemonUseCaseImpl::class.java)
    
    override fun execute(input: EvolvePokemonInput): PokemonOutput {
        logger.info("Attempting to evolve Pokemon with ID: {}", input.pokemonId)
        
        // Buscar o Pokémon
        val pokemon = pokemonRepository.findById(input.pokemonId)
            ?: throw PokemonNotFoundException("Pokemon with ID ${input.pokemonId} not found")
        
        // Verificar se pode evoluir
        if (!pokemon.canEvolve()) {
            throw InvalidPokemonException("${pokemon.name} cannot evolve (no evolution chain)")
        }
        
        // Lógica de evolução (movida do service para o use case)
        // TODO: Implementar lógica real baseada em evolutionChain
        // Por enquanto, apenas simula evolução alterando nome
        val evolved = pokemon.copy(
            name = "${pokemon.name} (Evolved)"
        )
        
        // Salvar o Pokémon evoluído
        val savedEvolved = pokemonRepository.save(evolved)
        
        logger.info("Pokemon evolved successfully: {} -> {}", pokemon.name, savedEvolved.name)
        
        return PokemonOutput.fromDomain(savedEvolved)
    }
}
