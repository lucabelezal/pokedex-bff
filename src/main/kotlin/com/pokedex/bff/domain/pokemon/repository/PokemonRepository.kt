package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.shared.Page

interface PokemonRepository {
    fun findById(id: String): Pokemon?
    fun save(pokemon: Pokemon): Pokemon
    fun findAll(page: Int, size: Int): Page<Pokemon>
}
