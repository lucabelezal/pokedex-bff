package com.pokedex.bff.application.usecase

import com.pokedex.bff.application.dtos.input.CreatePokemonInput
import com.pokedex.bff.application.dtos.output.PokemonOutput
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.shared.Page

interface CreatePokemonUseCase {
    fun execute(input: CreatePokemonInput): PokemonOutput
    fun findAll(page: Int, size: Int): Page<Pokemon>
}
