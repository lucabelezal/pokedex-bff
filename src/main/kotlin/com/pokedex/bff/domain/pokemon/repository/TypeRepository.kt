package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.Type

interface TypeRepository {
    fun findById(id: Long): Type?
    fun save(type: Type): Type
    fun findAll(): List<Type>
}
