package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.Region

interface RegionRepository {
    fun findById(id: Long): Region?
    fun save(region: Region): Region
    fun findAll(): List<Region>
}
