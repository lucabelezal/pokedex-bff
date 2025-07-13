package com.pokedex.bff.infrastructure.seeder.dto

import com.pokedex.bff.domain.entities.AbilityEntity
import com.pokedex.bff.domain.entities.EggGroupEntity
import com.pokedex.bff.domain.entities.EvolutionChainEntity
import com.pokedex.bff.domain.entities.GenerationEntity
import com.pokedex.bff.domain.entities.RegionEntity
import com.pokedex.bff.domain.entities.SpeciesEntity
import com.pokedex.bff.domain.entities.StatsEntity
import com.pokedex.bff.domain.entities.TypeEntity

data class PokemonImportRelations(
    val regions: Map<Long, RegionEntity>,
    val stats: Map<Long, StatsEntity>,
    val generations: Map<Long, GenerationEntity>,
    val species: Map<Long, SpeciesEntity>,
    val eggGroups: Map<Long, EggGroupEntity>,
    val types: Map<Long, TypeEntity>,
    val abilities: Map<Long, AbilityEntity>,
    val evolutionChains: Map<Long, EvolutionChainEntity>
)
