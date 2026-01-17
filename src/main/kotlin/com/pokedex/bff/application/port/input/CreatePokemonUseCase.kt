package com.pokedex.bff.application.port.input

import com.pokedex.bff.application.dtos.input.CreatePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput

/**
 * Port (interface) para o caso de uso de criação de Pokémon.
 * Define o contrato que o adapter de entrada (controller) usa.
 */
interface CreatePokemonUseCase {
    fun execute(input: CreatePokemonInput): PokemonOutput
}
