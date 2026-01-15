package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.Species

interface SpeciesRepository {
    fun findById(id: Long): Species?
    fun save(species: Species): Species
    fun findAll(): List<Species>
}
