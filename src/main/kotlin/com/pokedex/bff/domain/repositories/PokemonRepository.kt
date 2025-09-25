package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.common.Page

interface PokemonRepository {
    fun findById(id: Long): Pokemon?
    fun findAll(): List<Pokemon>
    fun findAll(page: Int, size: Int): Page<Pokemon>
    fun save(pokemon: Pokemon): Pokemon
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}