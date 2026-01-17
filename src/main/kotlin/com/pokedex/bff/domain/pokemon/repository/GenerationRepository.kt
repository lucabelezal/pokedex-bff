package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.Generation

interface GenerationRepository {
    fun findById(id: Long): Generation?
    fun save(generation: Generation): Generation
    fun findAll(): List<Generation>
}
