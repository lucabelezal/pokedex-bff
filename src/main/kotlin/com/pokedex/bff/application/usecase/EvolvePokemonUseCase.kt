package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.dtos.input.EvolvePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput

interface EvolvePokemonUseCase {
    fun execute(input: EvolvePokemonInput): PokemonOutput
}
