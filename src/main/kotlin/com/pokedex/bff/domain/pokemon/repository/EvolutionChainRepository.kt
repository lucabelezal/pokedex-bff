package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.EvolutionChain

interface EvolutionChainRepository {
    fun findById(id: Long): EvolutionChain?
    fun save(evolutionChain: EvolutionChain): EvolutionChain
    fun findAll(): List<EvolutionChain>
}
