package com.pokedex.bff.domain.repository

import com.pokedex.bff.domain.entities.Pokemon

interface PokemonRepository {
    fun findById(id: Long): Pokemon?
    fun findAll(page: Int, size: Int): List<Pokemon>
    // Add other methods needed for the domain
}

