package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.Ability

interface AbilityRepository {
    fun findById(id: Long): Ability?
    fun save(ability: Ability): Ability
    fun findAll(): List<Ability>
}
