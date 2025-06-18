package com.pokedex.bff.infra.repository

import com.pokedex.bff.infra.entity.Ability
import com.pokedex.bff.infra.entity.EggGroup
import com.pokedex.bff.infra.entity.EvolutionChain
import com.pokedex.bff.infra.entity.Generation
import com.pokedex.bff.infra.entity.Pokemon
import com.pokedex.bff.infra.entity.PokemonAbility
import com.pokedex.bff.infra.entity.Region
import com.pokedex.bff.infra.entity.Species
import com.pokedex.bff.infra.entity.Stats
import com.pokedex.bff.infra.entity.Type
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

interface RegionRepository : JpaRepository<Region, Long>
interface EggGroupRepository : JpaRepository<EggGroup, Long>
interface GenerationRepository : JpaRepository<Generation, Long>
interface AbilityRepository : JpaRepository<Ability, Long>
interface SpeciesRepository : JpaRepository<Species, Long>
interface StatsRepository : JpaRepository<Stats, Long>
interface EvolutionChainRepository : JpaRepository<EvolutionChain, Long>
interface PokemonAbilityRepository : JpaRepository<PokemonAbility, Long>

interface TypeRepository : JpaRepository<Type, Long> {
    fun findByNameIn(names: List<String>): List<Type>
}

@Repository
interface PokemonRepository : JpaRepository<Pokemon, Long> {
    fun findByName(name: String): Pokemon?
    fun findByIdIn(ids: List<Long>): List<Pokemon>
    @Query("SELECT p FROM Pokemon p JOIN FETCH p.types")
    fun findAllWithTypes(): List<Pokemon>
    override fun findAll(pageable: Pageable): Page<Pokemon>
}