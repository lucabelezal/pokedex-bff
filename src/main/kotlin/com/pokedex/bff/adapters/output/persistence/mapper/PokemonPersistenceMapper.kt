package com.pokedex.bff.adapters.output.persistence.mapper

import com.pokedex.bff.adapters.output.persistence.entity.*
import com.pokedex.bff.domain.pokemon.entities.*
import org.springframework.stereotype.Component

@Component
class PokemonPersistenceMapper {

    fun toEntity(pokemon: Pokemon): PokemonJpaEntity =
        PokemonJpaEntity(
            id = pokemon.id,
            number = pokemon.number,
            name = pokemon.name,
            height = pokemon.height,
            weight = pokemon.weight,
            description = pokemon.description,
            sprites = pokemon.sprites,
            genderRateValue = pokemon.genderRateValue,
            genderMale = pokemon.genderMale,
            genderFemale = pokemon.genderFemale,
            eggCycles = pokemon.eggCycles,
            stats = pokemon.stats?.let {
                StatsEmbeddable(
                    total = it.total,
                    hp = it.hp,
                    attack = it.attack,
                    defense = it.defense,
                    spAtk = it.spAtk,
                    spDef = it.spDef,
                    speed = it.speed
                )
            },
            generation = pokemon.generation?.let { toGenerationEntity(it) },
            species = pokemon.species?.let { toSpeciesEntity(it) },
            region = pokemon.region?.let { toRegionEntity(it) },
            evolutionChain = pokemon.evolutionChain?.let { toEvolutionChainEntity(it) },
            types = pokemon.types.map { toTypeEntity(it) }.toSet(),
            eggGroups = pokemon.eggGroups.map { toEggGroupEntity(it) }.toSet(),
            weaknesses = pokemon.weaknesses.map { toTypeEntity(it) }.toSet(),
            abilities = emptySet() // Mapeado via OneToMany
        )

    fun toDomain(entity: PokemonJpaEntity): Pokemon =
        Pokemon(
            id = entity.id,
            number = entity.number,
            name = entity.name,
            height = entity.height,
            weight = entity.weight,
            description = entity.description,
            sprites = entity.sprites,
            genderRateValue = entity.genderRateValue,
            genderMale = entity.genderMale,
            genderFemale = entity.genderFemale,
            eggCycles = entity.eggCycles,
            stats = entity.stats?.let {
                Stats(
                    id = entity.id,
                    total = it.total,
                    hp = it.hp,
                    attack = it.attack,
                    defense = it.defense,
                    spAtk = it.spAtk,
                    spDef = it.spDef,
                    speed = it.speed
                )
            },
            generation = entity.generation?.let { toGenerationDomain(it) },
            species = entity.species?.let { toSpeciesDomain(it) },
            region = entity.region?.let { toRegionDomain(it) },
            evolutionChain = entity.evolutionChain?.let { toEvolutionChainDomain(it) },
            types = entity.types.map { toTypeDomain(it) }.toSet(),
            abilities = entity.abilities.map { toPokemonAbilityDomain(it) }.toSet(),
            eggGroups = entity.eggGroups.map { toEggGroupDomain(it) }.toSet(),
            weaknesses = entity.weaknesses.map { toTypeDomain(it) }.toSet()
        )

    // Helper mappers - Entity to JPA
    private fun toTypeEntity(type: Type): TypeJpaEntity =
        TypeJpaEntity(id = type.id, name = type.name, color = type.color)

    private fun toRegionEntity(region: Region): RegionJpaEntity =
        RegionJpaEntity(id = region.id, name = region.name)

    private fun toGenerationEntity(generation: Generation): GenerationJpaEntity =
        GenerationJpaEntity(
            id = generation.id,
            name = generation.name,
            region = generation.region?.let { toRegionEntity(it) }
        )

    private fun toSpeciesEntity(species: Species): SpeciesJpaEntity =
        SpeciesJpaEntity(
            id = species.id,
            pokemonNumber = species.pokemonNumber,
            name = species.name,
            speciesEn = species.speciesEn,
            speciesPt = species.speciesPt
        )

    private fun toEggGroupEntity(eggGroup: EggGroup): EggGroupJpaEntity =
        EggGroupJpaEntity(id = eggGroup.id, name = eggGroup.name)

    private fun toEvolutionChainEntity(evolutionChain: EvolutionChain): EvolutionChainJpaEntity =
        EvolutionChainJpaEntity(
            id = evolutionChain.id,
            chainData = evolutionChain.chainData
        )

    // Helper mappers - JPA to Domain
    private fun toTypeDomain(entity: TypeJpaEntity): Type =
        Type(id = entity.id, name = entity.name, color = entity.color)

    private fun toRegionDomain(entity: RegionJpaEntity): Region =
        Region(id = entity.id, name = entity.name)

    private fun toGenerationDomain(entity: GenerationJpaEntity): Generation =
        Generation(
            id = entity.id,
            name = entity.name,
            region = entity.region?.let { toRegionDomain(it) }
        )

    private fun toSpeciesDomain(entity: SpeciesJpaEntity): Species =
        Species(
            id = entity.id,
            pokemonNumber = entity.pokemonNumber,
            name = entity.name,
            speciesEn = entity.speciesEn,
            speciesPt = entity.speciesPt
        )

    private fun toEggGroupDomain(entity: EggGroupJpaEntity): EggGroup =
        EggGroup(id = entity.id, name = entity.name)

    private fun toEvolutionChainDomain(entity: EvolutionChainJpaEntity): EvolutionChain =
        EvolutionChain(
            id = entity.id,
            chainData = entity.chainData
        )

    private fun toPokemonAbilityDomain(entity: PokemonAbilityJpaEntity): PokemonAbility {
        // Não inclui referência ao Pokemon para evitar ciclos circulares
        // O Pokemon já contém Set<PokemonAbility>, então a relação é unidirecional
        return PokemonAbility(
            id = entity.id,
            ability = Ability(
                id = entity.ability.id,
                name = entity.ability.name,
                description = entity.ability.description,
                introducedGeneration = entity.ability.introducedGeneration?.let { toGenerationDomain(it) }
            ),
            isHidden = entity.isHidden
        )
    }
}
