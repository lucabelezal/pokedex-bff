package com.pokedex.bff.infrastructure.repository

import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.repository.PokemonRepository
import com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface SpringDataPokemonRepository : JpaRepository<PokemonEntity, Long>

@Repository
class JpaPokemonRepository(
    private val springDataPokemonRepository: SpringDataPokemonRepository
) : PokemonRepository {
    override fun findById(id: Long): Pokemon? =
        springDataPokemonRepository.findById(id).orElse(null)?.toDomain()

    override fun findAll(page: Int, size: Int): List<Pokemon> =
        springDataPokemonRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size))
            .map { it.toDomain() }
            .toList()
}

// Extension function to convert PokemonEntity to domain Pokemon
fun PokemonEntity.toDomain(): Pokemon = Pokemon(
    id = this.id,
    number = this.number,
    name = this.name,
    height = this.height,
    weight = this.weight,
    description = this.description,
    sprites = null, // Map as needed
    genderRateValue = this.genderRateValue,
    genderMale = this.genderMale,
    genderFemale = this.genderFemale,
    eggCycles = this.eggCycles,
    stats = null, // Map as needed
    generation = null, // Map as needed
    species = null, // Map as needed
    region = null, // Map as needed
    evolutionChain = null, // Map as needed
    types = emptySet(), // Map as needed
    abilities = emptySet(), // Map as needed
    eggGroups = emptySet(), // Map as needed
    weaknesses = emptySet() // Map as needed
)

