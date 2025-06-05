package com.pokedex.bff.repositories


import com.pokedex.bff.models.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query // Importe a anotação Query
import org.springframework.data.repository.query.Param // Importe a anotação Param
import org.springframework.stereotype.Repository

@Repository
interface RegionRepository : JpaRepository<Region, Int>

@Repository
interface TypeRepository : JpaRepository<Type, Int>

@Repository
interface EggGroupRepository : JpaRepository<EggGroup, Int>

@Repository
interface SpeciesRepository : JpaRepository<Species, Int> {
    fun findByNationalPokedexNumber(nationalPokedexNumber: String): Species?
}

@Repository
interface GenerationRepository : JpaRepository<Generation, Int>

@Repository
interface AbilityRepository : JpaRepository<Ability, Int>

@Repository
interface StatsRepository : JpaRepository<Stats, Int> {
    fun findByPokemonNationalPokedexNumber(pokemonNationalPokedexNumber: String): Stats?
}

@Repository
interface EvolutionChainRepository : JpaRepository<EvolutionChain, Int>

@Repository
interface PokemonRepository : JpaRepository<Pokemon, Int> {
    fun findByNationalPokedexNumber(nationalPokedexNumber: String): Pokemon?
    fun findByNameContainingIgnoreCase(name: String): List<Pokemon>
}

@Repository
interface PokemonTypeRepository : JpaRepository<PokemonType, Int> {
    fun findByPokemonId(pokemonId: Int): List<PokemonType>
    fun findByTypeId(typeId: Int): List<PokemonType>
}

@Repository
interface PokemonAbilityRepository : JpaRepository<PokemonAbility, Int> {
    fun findByPokemonId(pokemonId: Int): List<PokemonAbility>
    fun findByAbilityId(abilityId: Int): List<PokemonAbility>
}

@Repository
interface PokemonEggGroupRepository : JpaRepository<PokemonEggGroup, Int> {
    fun findByPokemonId(pokemonId: Int): List<PokemonEggGroup>
    fun findByEggGroupId(eggGroupId: Int): List<PokemonEggGroup>
}

@Repository
interface WeaknessRepository : JpaRepository<Weakness, Int> {
    fun findByPokemonId(pokemonId: Int): List<Weakness>

    // <--- ALTERADO AQUI: Usando @Query para especificar o nome da propriedade 'weakness_type'
    @Query("SELECT w FROM Weakness w WHERE w.weakness_type = :weaknessTypeParam")
    fun findByWeaknessType(@Param("weaknessTypeParam") weaknessType: String): List<Weakness>
}

@Repository
interface EvolutionDetailRepository : JpaRepository<EvolutionDetail, Int> {
    fun findByEvolutionChainId(evolutionChainId: Int): List<EvolutionDetail>
    fun findByPokemonId(pokemonId: Int): List<EvolutionDetail> // Add if needed for lookup
}