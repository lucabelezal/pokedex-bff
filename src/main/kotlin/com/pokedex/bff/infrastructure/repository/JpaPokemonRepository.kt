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

// Função de extensão para converter PokemonEntity em Pokemon do domínio
fun PokemonEntity.toDomain(): Pokemon = Pokemon(
    id = this.id,
    number = this.number,
    name = this.name,
    height = this.height,
    weight = this.weight,
    description = this.description,
    sprites = null, // Mapear conforme necessário
    genderRateValue = this.genderRateValue,
    genderMale = this.genderMale,
    genderFemale = this.genderFemale,
    eggCycles = this.eggCycles,
    stats = null, // Mapear conforme necessário
    generation = null, // Mapear conforme necessário
    species = null, // Mapear conforme necessário
    region = null, // Mapear conforme necessário
    evolutionChain = null, // Mapear conforme necessário
    types = emptySet(), // Mapear conforme necessário
    abilities = emptySet(), // Mapear conforme necessário
    eggGroups = emptySet(), // Mapear conforme necessário
    weaknesses = emptySet() // Mapear conforme necessário
)

