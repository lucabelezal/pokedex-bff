package com.pokedex.bff.application.port.input

import com.pokedex.bff.application.dtos.input.EvolvePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput

/**
 * Port (interface) para o caso de uso de evolução de Pokémon.
 * Define o contrato que o adapter de entrada (controller) usa.
 */
interface EvolvePokemonUseCase {
    fun execute(input: EvolvePokemonInput): PokemonOutput
}
