package com.pokedex.bff.repositories

import com.pokedex.bff.models.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// --- Repositórios Core ---

@Repository
interface RegionRepository : JpaRepository<Region, Int>

@Repository
interface TypeRepository : JpaRepository<Type, Int> {
    fun findByName(name: String): Type?
}

@Repository
interface EggGroupRepository : JpaRepository<EggGroup, Int>

@Repository
interface SpeciesRepository : JpaRepository<Species, Int> {
    fun findByNationalPokedexNumber(nationalPokedexNumber: String): Species?
}

@Repository
interface GenerationRepository : JpaRepository<Generation, Int>

@Repository
interface AbilityRepository : JpaRepository<Ability, String>

@Repository
interface StatsRepository : JpaRepository<Stats, Int> {
    fun findByPokemonNationalPokedexNumber(pokemonNationalPokedexNumber: String): Stats?
}

@Repository
interface PokemonRepository : JpaRepository<Pokemon, Int> {
    fun findByNationalPokedexNumber(nationalPokedexNumber: String): Pokemon?
    fun findByNameContainingIgnoreCase(name: String): List<Pokemon>
}

// --- Repositórios para Tabelas de Junção ---

@Repository
interface PokemonTypeRepository : JpaRepository<PokemonType, Int>

@Repository
interface PokemonAbilityRepository : JpaRepository<PokemonAbility, Int>

@Repository
interface PokemonEggGroupRepository : JpaRepository<PokemonEggGroup, Int>

@Repository
interface PokemonWeaknessRepository : JpaRepository<PokemonWeakness, Int>

// --- Repositórios de Evolução ---

@Repository
interface EvolutionChainRepository : JpaRepository<EvolutionChain, Int>

@Repository
interface EvolutionLinkRepository : JpaRepository<EvolutionLink, EvolutionLinkPk>
