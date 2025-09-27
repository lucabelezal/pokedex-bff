package com.pokedex.bff.adapters.gateways

import com.pokedex.bff.domain.entities.Ability
import com.pokedex.bff.domain.entities.EvolutionChain
import com.pokedex.bff.domain.entities.Generation
import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.entities.PokemonAbility
import com.pokedex.bff.domain.entities.Region
import com.pokedex.bff.domain.entities.Species
import com.pokedex.bff.domain.entities.Sprites
import com.pokedex.bff.domain.entities.Stats
import com.pokedex.bff.domain.entities.Type
import com.pokedex.bff.domain.entities.OtherSprites
import com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity

object PokemonMapper {
    fun toDomain(entity: PokemonEntity): Pokemon =
        Pokemon(
            id = entity.id,
            number = entity.number,
            name = entity.name,
            height = entity.height,
            weight = entity.weight,
            description = entity.description,
            sprites = mapSprites(entity),
            genderRateValue = entity.genderRateValue,
            genderMale = entity.genderMale,
            genderFemale = entity.genderFemale,
            eggCycles = entity.eggCycles,
            stats = mapStats(entity),
            generation = mapGeneration(entity),
            species = mapSpecies(entity),
            region = entity.region?.let { Region(it.id, it.name) },
            evolutionChain = mapEvolutionChain(entity),
            types = mapTypes(entity),
            abilities = mapAbilities(entity),
            eggGroups = com.pokedex.bff.adapters.gateways.mapEggGroups(entity),
            weaknesses = com.pokedex.bff.adapters.gateways.mapWeaknesses(entity)
        )

    private fun mapSprites(entity: PokemonEntity): Sprites? =
        entity.sprites?.let { spritesDto ->
            Sprites(
                backDefault = spritesDto.backDefault,
                backFemale = spritesDto.backFemale,
                backShiny = spritesDto.backShiny,
                backShinyFemale = spritesDto.backShinyFemale,
                frontDefault = spritesDto.frontDefault,
                frontFemale = spritesDto.frontFemale,
                frontShiny = spritesDto.frontShiny,
                frontShinyFemale = spritesDto.frontShinyFemale,
                other = spritesDto.other?.let { oDto ->
                    OtherSprites(
                        dreamWorld = oDto.dreamWorld,
                        home = oDto.home,
                        officialArtwork = oDto.officialArtwork,
                        showdown = oDto.showdown
                    )
                }
            )
        }

    private fun mapStats(entity: PokemonEntity): Stats? =
        entity.stats?.let {
            Stats(
                id = it.id,
                total = it.total,
                hp = it.hp,
                attack = it.attack,
                defense = it.defense,
                spAtk = it.spAtk,
                spDef = it.spDef,
                speed = it.speed
            )
        }

    private fun mapGeneration(entity: PokemonEntity): Generation? =
        entity.generation?.let {
            Generation(
                id = it.id,
                name = it.name,
                region = it.region?.let { r -> Region(r.id, r.name) }
            )
        }

    private fun mapSpecies(entity: PokemonEntity): Species? =
        entity.species?.let {
            Species(
                id = it.id,
                pokemonNumber = it.pokemonNumber,
                name = it.name,
                speciesEn = it.speciesEn,
                speciesPt = it.speciesPt
            )
        }

    private fun mapEvolutionChain(entity: PokemonEntity): EvolutionChain? =
        entity.evolutionChain?.let { EvolutionChain(id = it.id, chainData = it.chainData) }

    private fun mapTypes(entity: PokemonEntity): Set<Type> =
        entity.types.map { Type(id = it.id, name = it.name, color = it.color) }.toSet()

    private fun mapAbilities(entity: PokemonEntity): Set<PokemonAbility> =
        entity.abilities.map {
            PokemonAbility(
                id = it.id,
                pokemon = entity.toDomainMinimal(),
                ability = Ability(
                    id = it.ability.id,
                    name = it.ability.name,
                    description = it.ability.description,
                    introducedGeneration = null
                ),
                isHidden = it.isHidden
            )
        }.toSet()

    // weaknesses and egg groups mapping are delegated to top-level helpers to keep this object small

    // Reuse small minimal mapping used by abilities to avoid cycles
    private fun PokemonEntity.toDomainMinimal(): Pokemon =
        Pokemon(
            id = this.id,
            number = this.number,
            name = this.name,
            height = this.height,
            weight = this.weight,
            description = this.description,
            sprites = null,
            genderRateValue = this.genderRateValue,
            genderMale = this.genderMale,
            genderFemale = this.genderFemale,
            eggCycles = this.eggCycles,
            stats = null,
            generation = null,
            species = null,
            region = null,
            evolutionChain = null,
            types = emptySet(),
            abilities = emptySet(),
            eggGroups = emptySet(),
            weaknesses = emptySet()
        )

    
}

