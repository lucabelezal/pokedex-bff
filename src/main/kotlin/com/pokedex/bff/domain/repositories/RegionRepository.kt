package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Region

interface RegionRepository {
    fun findById(id: Long): Region?
    fun findAll(): List<Region>
    fun save(region: Region): Region
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
