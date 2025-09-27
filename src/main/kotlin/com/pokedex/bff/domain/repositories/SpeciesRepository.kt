package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Species

interface SpeciesRepository {
    fun findById(id: Long): Species?
    fun findAll(): List<Species>
    fun save(species: Species): Species
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
