package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.Stats

interface StatsRepository {
    fun findById(id: Long): Stats?
    fun save(stats: Stats): Stats
    fun findAll(): List<Stats>
}
