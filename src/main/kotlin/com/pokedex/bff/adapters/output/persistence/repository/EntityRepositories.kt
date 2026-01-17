package com.pokedex.bff.adapters.output.persistence.repository

import com.pokedex.bff.adapters.output.persistence.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegionJpaRepository : JpaRepository<RegionJpaEntity, Long>

@Repository
interface GenerationJpaRepository : JpaRepository<GenerationJpaEntity, Long>

@Repository
interface AbilityJpaRepository : JpaRepository<AbilityJpaEntity, Long>

@Repository
interface SpeciesJpaRepository : JpaRepository<SpeciesJpaEntity, Long>

@Repository
interface EggGroupJpaRepository : JpaRepository<EggGroupJpaEntity, Long>

@Repository
interface EvolutionChainJpaRepository : JpaRepository<EvolutionChainJpaEntity, Long>

@Repository
interface TypeJpaRepository : JpaRepository<TypeJpaEntity, Long>

@Repository
interface PokemonAbilityJpaRepository : JpaRepository<PokemonAbilityJpaEntity, Long>
