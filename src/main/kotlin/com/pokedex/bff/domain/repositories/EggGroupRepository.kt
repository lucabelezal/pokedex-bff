
package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.EggGroup

interface EggGroupRepository {
    fun findById(id: Long): EggGroup?
    fun findAll(): List<EggGroup>
    fun save(eggGroup: EggGroup): EggGroup
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
