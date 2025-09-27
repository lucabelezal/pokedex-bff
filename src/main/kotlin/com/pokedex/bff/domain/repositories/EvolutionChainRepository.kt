package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.EvolutionChain

interface EvolutionChainRepository {
    fun findById(id: Long): EvolutionChain?
    fun findAll(): List<EvolutionChain>
    fun save(evolutionChain: EvolutionChain): EvolutionChain
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
