package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.EggGroup

interface EggGroupRepository {
    fun findById(id: Long): EggGroup?
    fun save(eggGroup: EggGroup): EggGroup
    fun findAll(): List<EggGroup>
}
