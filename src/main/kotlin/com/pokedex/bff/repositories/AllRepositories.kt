package com.pokedex.bff.repositories

import com.pokedex.bff.models.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// Repositórios para entidades com chave primária simples (Int)

@Repository
interface SuperContestEffectRepository : JpaRepository<SuperContestEffect, Int>

@Repository
interface RegionRepository : JpaRepository<Region, Int>

@Repository
interface GenerationRepository : JpaRepository<Generation, Int>

@Repository
interface AbilityRepository : JpaRepository<Ability, Int>

@Repository
interface DamageClassRepository : JpaRepository<DamageClass, Int>

@Repository
interface TypeRepository : JpaRepository<Type, Int>

@Repository
interface StatRepository : JpaRepository<Stat, Int>

@Repository
interface GrowthRateRepository : JpaRepository<GrowthRate, Int>

@Repository
interface EggGroupRepository : JpaRepository<EggGroup, Int>

@Repository
interface GenderRepository : JpaRepository<Gender, Int>

@Repository
interface CharacteristicRepository : JpaRepository<Characteristic, Int>

@Repository
interface FlavorRepository : JpaRepository<Flavor, Int>

@Repository
interface NatureRepository : JpaRepository<Nature, Int>

@Repository
interface MoveTargetRepository : JpaRepository<MoveTarget, Int>

@Repository
interface MoveEffectRepository : JpaRepository<MoveEffect, Int>

@Repository
interface LanguageRepository : JpaRepository<Language, Int>

@Repository
interface MoveRepository : JpaRepository<Move, Int>

@Repository
interface PokemonColorRepository : JpaRepository<PokemonColor, Int>

@Repository
interface PokemonShapeRepository : JpaRepository<PokemonShape, Int>

@Repository
interface PokemonHabitatRepository : JpaRepository<PokemonHabitat, Int>

@Repository
interface EvolutionChainRepository : JpaRepository<EvolutionChain, Int>

@Repository
interface PokemonSpeciesRepository : JpaRepository<PokemonSpecies, Int>

@Repository
interface PokemonRepository : JpaRepository<Pokemon, Int>

@Repository
interface VersionGroupRepository : JpaRepository<VersionGroup, Int>

@Repository
interface PokemonFormRepository : JpaRepository<PokemonForm, Int>

@Repository
interface LocationRepository : JpaRepository<Location, Int>

@Repository
interface PokemonLocationAreaRepository : JpaRepository<LocationArea, Int>

@Repository
interface ContestTypeRepository : JpaRepository<ContestType, Int>

@Repository
interface ContestEffectRepository : JpaRepository<ContestEffect, Int>

@Repository
interface ItemCategoryRepository : JpaRepository<ItemCategory, Int>

@Repository
interface ItemRepository : JpaRepository<Item, Int>

@Repository
interface PokemonMoveMethodRepository : JpaRepository<PokemonMoveMethod, Int>

@Repository
interface BerryRepository : JpaRepository<Berry, Int>


// Repositórios para entidades com chave primária composta (necessitam da classe EmbeddableId)

@Repository
interface MoveEffectProseRepository : JpaRepository<MoveEffectProse, MoveEffectProseId>

@Repository
interface PokemonStatRepository : JpaRepository<PokemonStat, PokemonStatId>

@Repository
interface PokemonTypeRepository : JpaRepository<PokemonType, PokemonTypeId>

@Repository
interface PokemonAbilityRepository : JpaRepository<PokemonAbility, PokemonAbilityId>

@Repository
interface PokemonEggGroupRepository : JpaRepository<PokemonEggGroup, PokemonEggGroupId>

@Repository
interface AbilityProseRepository : JpaRepository<AbilityProse, AbilityProseId>

@Repository
interface AbilityFlavorTextRepository : JpaRepository<AbilityFlavorText, AbilityFlavorTextId>

@Repository
interface PokemonMoveRepository : JpaRepository<PokemonMove, PokemonMoveId>

@Repository
interface BerryFlavorRepository : JpaRepository<BerryFlavor, BerryFlavorId>