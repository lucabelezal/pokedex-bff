package com.pokedex.bff.repositories

import com.pokedex.bff.models.* // Import all your updated entities and PK classes
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// --- Repositórios Core ---

@Repository
interface RegionRepository : JpaRepository<Region, Int>

@Repository
interface TypeRepository : JpaRepository<Type, Int> {
    // Still useful to find Type by name
    fun findByName(name: String): Type?
}

@Repository
interface EggGroupRepository : JpaRepository<EggGroup, Int>

@Repository
interface SpeciesRepository : JpaRepository<Species, Int> {
    // Find the Species entity by its unique National Pokedex Number
    fun findByNationalPokedexNumber(nationalPokedexNumber: String): Species?
}

@Repository
interface GenerationRepository : JpaRepository<Generation, Int>

@Repository
interface AbilityRepository : JpaRepository<Ability, Int> { // Changed PK type to Int
    // Still useful to find Ability by name
    fun findByName(name: String): Ability?
}

@Repository
// Stats entity PK is now pokemonId (Int)
interface StatsRepository : JpaRepository<Stats, Int> {
    // Removed findByPokemonNationalPokedexNumber as that column is gone from Stats
    // You would typically find Stats by Pokemon.id
    // Or query Pokemon and navigate via the 1:1 relationship
}

@Repository
interface PokemonRepository : JpaRepository<Pokemon, Int> {
    // Return List<Pokemon> because national_pokedex_number is no longer unique for Pokemon entity
    fun findByNationalPokedexNumber(nationalPokedexNumber: String): List<Pokemon>
    fun findByNameContainingIgnoreCase(name: String): List<Pokemon>
    // findById(Int) is inherited from JpaRepository
}

// --- Repositórios para Tabelas de Junção (Usando Classes de Chave Composta) ---

@Repository
// PokemonType uses composite PK PokemonTypeId
interface PokemonTypeRepository : JpaRepository<PokemonType, PokemonTypeId>

@Repository
// PokemonAbility uses composite PK PokemonAbilityId
interface PokemonAbilityRepository : JpaRepository<PokemonAbility, PokemonAbilityId>

@Repository
// PokemonEggGroup uses composite PK PokemonEggGroupId
interface PokemonEggGroupRepository : JpaRepository<PokemonEggGroup, PokemonEggGroupId>

@Repository
// PokemonWeakness uses composite PK PokemonWeaknessId
interface PokemonWeaknessRepository : JpaRepository<PokemonWeakness, PokemonWeaknessId>

// --- Repositórios de Evolução ---

@Repository
interface EvolutionChainRepository : JpaRepository<EvolutionChain, Int>

@Repository
// EvolutionLink uses composite PK EvolutionLinkPk
interface EvolutionLinkRepository : JpaRepository<EvolutionLink, EvolutionLinkPk>