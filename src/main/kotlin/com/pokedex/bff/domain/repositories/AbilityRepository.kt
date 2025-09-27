package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Ability

interface AbilityRepository {
    fun findById(id: Long): Ability?
    fun findAll(): List<Ability>
    fun save(ability: Ability): Ability
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
