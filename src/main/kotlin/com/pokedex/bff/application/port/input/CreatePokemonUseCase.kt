package com.pokedex.bff.application.port.input

import com.pokedex.bff.application.dtos.input.CreatePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.shared.Page

/**
 * Port (interface) para o caso de uso de criação de Pokémon.
 * Define o contrato que o adapter de entrada (controller) usa.
 */
interface CreatePokemonUseCase {
    fun execute(input: CreatePokemonInput): PokemonOutput
    fun findAll(page: Int, size: Int): Page<Pokemon>
}
