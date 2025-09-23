package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Generation

interface GenerationRepository {
    fun findById(id: Long): Generation?
    fun findAll(): List<Generation>
    fun save(generation: Generation): Generation
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}