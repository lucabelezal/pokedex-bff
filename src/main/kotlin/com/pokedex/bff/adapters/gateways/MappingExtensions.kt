package com.pokedex.bff.adapters.gateways

import com.pokedex.bff.domain.common.Page
import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.entities.EggGroup
import com.pokedex.bff.domain.entities.Type
import com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity
import org.springframework.data.domain.Page as SpringPage

// Partial mapping (main attributes only). Keep simple persistence mapping here to
// avoid increasing the size of the PokemonMapper object.
fun toEntityPartial(domain: Pokemon): PokemonEntity =
    PokemonEntity(
        id = domain.id,
        number = domain.number,
        name = domain.name,
        height = domain.height,
        weight = domain.weight,
        description = domain.description,
        sprites = null, // Sprites persistence omitted for now
        genderRateValue = domain.genderRateValue,
        genderMale = domain.genderMale,
        genderFemale = domain.genderFemale,
        eggCycles = domain.eggCycles,
        stats = null,          // Stats persistence omitted for now
        generation = null,     // Generation persistence omitted for now
        species = null,        // Species persistence omitted for now
        region = null,         // Region persistence omitted for now
        evolutionChain = null, // EvolutionChain persistence omitted for now
        types = mutableSetOf(),
        abilities = mutableSetOf(),
        eggGroups = mutableSetOf(),
        weaknesses = mutableSetOf()
    )


fun toDomainPage(page: SpringPage<PokemonEntity>): Page<Pokemon> =
    Page(
        content = page.content.map { PokemonMapper.toDomain(it) },
        pageNumber = page.number,
        pageSize = page.size,
        totalElements = page.totalElements,
        totalPages = page.totalPages,
        isFirst = page.isFirst,
        isLast = page.isLast,
        hasNext = page.hasNext(),
        hasPrevious = page.hasPrevious()
    )


// Move egg groups mapping here to reduce functions count inside PokemonMapper
fun mapEggGroups(entity: PokemonEntity): Set<EggGroup> =
    entity.eggGroups.map { EggGroup(id = it.id, name = it.name) }.toSet()


fun mapWeaknesses(entity: PokemonEntity): Set<Type> =
    entity.weaknesses.map { Type(id = it.id, name = it.name, color = it.color) }.toSet()




