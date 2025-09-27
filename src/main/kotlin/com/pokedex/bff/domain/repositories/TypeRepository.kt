package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Type

interface TypeRepository {
    fun findById(id: Long): Type?
    fun findAll(): List<Type>
    fun save(type: Type): Type
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
