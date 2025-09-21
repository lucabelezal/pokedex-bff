package com.pokedex.bff.domain.repository

import com.pokedex.bff.domain.model.Pokemon

interface PokemonRepository {
    fun findById(id: Long): Pokemon?
    fun findAll(page: Int, size: Int): List<Pokemon>
    // Adicione outros métodos necessários para o domínio
}

