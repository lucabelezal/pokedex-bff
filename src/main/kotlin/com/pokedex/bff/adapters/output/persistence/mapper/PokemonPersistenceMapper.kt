package com.pokedex.bff.adapters.output.persistence.mapper

import com.pokedex.bff.adapters.output.persistence.entity.PokemonJpaEntity
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import org.springframework.stereotype.Component

@Component
class PokemonPersistenceMapper {
    fun toEntity(pokemon: Pokemon): PokemonJpaEntity =
        PokemonJpaEntity(
            id = pokemon.id.toString(),
            name = pokemon.name,
            type = pokemon.types.firstOrNull()?.toString() ?: "",
            level = 0
        )

    fun toDomain(entity: PokemonJpaEntity): Pokemon =
        Pokemon(
            id = entity.id.toLongOrNull() ?: 0L,
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
            types = emptySet(),
            abilities = emptySet(),
            eggGroups = emptySet(),
            weaknesses = emptySet()
        )
}
