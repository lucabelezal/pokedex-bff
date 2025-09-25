package com.pokedex.bff.infrastructure.repository

import com.pokedex.bff.domain.common.Page
import com.pokedex.bff.domain.entities.*
import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity
import com.pokedex.bff.infrastructure.persistence.repositories.SpringDataPokemonRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class JpaPokemonRepository(
    private val springDataRepository: SpringDataPokemonRepository
) : PokemonRepository {

    override fun findById(id: Long): Pokemon? {
        return springDataRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAll(): List<Pokemon> {
        return springDataRepository.findAll().map { it.toDomain() }
    }

    override fun findAll(page: Int, size: Int): Page<Pokemon> {
        val pageable = PageRequest.of(page, size)
        val springPage = springDataRepository.findAll(pageable)
        
        return Page(
            content = springPage.content.map { it.toDomain() },
            pageNumber = springPage.number,
            pageSize = springPage.size,
            totalElements = springPage.totalElements,
            totalPages = springPage.totalPages,
            isFirst = springPage.isFirst,
            isLast = springPage.isLast,
            hasNext = springPage.hasNext(),
            hasPrevious = springPage.hasPrevious()
        )
    }

    override fun save(pokemon: Pokemon): Pokemon {
        val entity = pokemon.toEntity()
        val savedEntity = springDataRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun existsById(id: Long): Boolean {
        return springDataRepository.existsById(id)
    }

    override fun deleteById(id: Long) {
        springDataRepository.deleteById(id)
    }

    // Extension functions para converter entre Entity e Domain
    private fun PokemonEntity.toDomain(): Pokemon {
        return Pokemon(
            id = this.id,
            number = this.number,
            name = this.name,
            height = this.height,
            weight = this.weight,
            description = this.description,
            sprites = this.sprites?.let { 
                Sprites(
                    backDefault = it.backDefault,
                    backFemale = it.backFemale,
                    backShiny = it.backShiny,
                    backShinyFemale = it.backShinyFemale,
                    frontDefault = it.frontDefault,
                    frontFemale = it.frontFemale,
                    frontShiny = it.frontShiny,
                    frontShinyFemale = it.frontShinyFemale,
                    other = it.other?.let { other ->
                        OtherSprites(
                            dreamWorld = other.dreamWorld?.frontDefault,
                            home = other.home?.frontDefault,
                            officialArtwork = other.officialArtwork?.frontDefault,
                            showdown = other.showdown?.frontDefault
                        )
                    }
                )
            },
            genderRateValue = this.genderRateValue,
            genderMale = this.genderMale,
            genderFemale = this.genderFemale,
            eggCycles = this.eggCycles,
            stats = this.stats?.let {
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
            },
            generation = this.generation?.let {
                Generation(
                    id = it.id,
                    name = it.name ?: "",
                    region = it.region?.let { r ->
                        Region(
                            id = r.id,
                            name = r.name ?: ""
                        )
                    }
                )
            },
            species = this.species?.let {
                Species(
                    id = it.id,
                    name = it.name ?: "",
                    pokemonNumber = it.pokemon_number,
                    speciesEn = it.speciesEn,
                    speciesPt = it.speciesPt
                )
            },
            region = this.region?.let {
                Region(
                    id = it.id,
                    name = it.name ?: ""
                )
            },
            evolutionChain = this.evolutionChain?.let {
                EvolutionChain(
                    id = it.id,
                    chainData = it.chainData
                )
            },
            types = this.types.map {
                Type(
                    id = it.id,
                    name = it.name ?: "",
                    color = it.color
                )
            }.toSet(),
            abilities = this.abilities.map {
                PokemonAbility(
                    id = it.id,
                    pokemon = this.toDomainMinimal(),
                    ability = Ability(
                        id = it.ability.id,
                        name = it.ability.name ?: "",
                        description = it.ability.description,
                        introducedGeneration = null
                    ),
                    isHidden = it.isHidden
                )
            }.toSet(),
            eggGroups = this.eggGroups.map {
                EggGroup(
                    id = it.id,
                    name = it.name ?: ""
                )
            }.toSet(),
            weaknesses = this.weaknesses.map {
                Type(
                    id = it.id,
                    name = it.name ?: "",
                    color = it.color
                )
            }.toSet()
        )
    }

    private fun PokemonEntity.toDomainMinimal(): Pokemon {
        return Pokemon(
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

    private fun Pokemon.toEntity(): PokemonEntity {
        return PokemonEntity(
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
            types = mutableSetOf(),
            abilities = mutableSetOf(),
            eggGroups = mutableSetOf(),
            weaknesses = mutableSetOf()
        )
    }
}