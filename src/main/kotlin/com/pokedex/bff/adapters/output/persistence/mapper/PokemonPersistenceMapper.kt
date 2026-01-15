package com.pokedex.bff.adapters.output.persistence.mapper

import com.pokedex.bff.adapters.output.persistence.entity.PokemonJpaEntity
import com.pokedex.bff.adapters.output.persistence.entity.TypeJpaEntity
import com.pokedex.bff.domain.pokemon.entities.Type
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import org.springframework.stereotype.Component

@Component
class PokemonPersistenceMapper {

    fun toEntity(pokemon: Pokemon): PokemonJpaEntity =
        PokemonJpaEntity(
            id = pokemon.id,
            name = pokemon.name,
            types = pokemon.types.map { TypeJpaEntity(it.id, it.name, it.color) }.toSet()
        )

    fun toDomain(entity: PokemonJpaEntity): Pokemon =
        Pokemon(
            id = entity.id,
            number = null,
            name = entity.name,
            height = null,
            weight = null,
            description = null,
            sprites = null,
            genderRateValue = null,
            genderMale = null,
            genderFemale = null,
            eggCycles = null,
            stats = null,
            generation = null,
            species = null,
            region = null,
            evolutionChain = null,
            types = entity.types.map { Type(it.id, it.name, it.color) }.toSet(),
            abilities = emptySet(),
            eggGroups = emptySet(),
            weaknesses = emptySet()
        )
}
