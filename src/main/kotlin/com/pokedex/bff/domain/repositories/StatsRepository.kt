
package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.Stats

interface StatsRepository {
    fun findById(id: Long): Stats?
    fun findAll(): List<Stats>
    fun save(stats: Stats): Stats
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
