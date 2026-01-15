package com.pokedex.bff.adapters.output.persistence.repository

import com.pokedex.bff.adapters.output.persistence.entity.PokemonJpaEntity
import com.pokedex.bff.adapters.output.persistence.mapper.PokemonPersistenceMapper
import com.pokedex.bff.domain.pokemon.entities.Pokemon
import com.pokedex.bff.domain.pokemon.repository.PokemonRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface SpringDataPokemonRepository : JpaRepository<PokemonJpaEntity, String>

@Repository
class PokemonRepositoryAdapter(
    private val springDataPokemonRepository: SpringDataPokemonRepository,
    private val mapper: PokemonPersistenceMapper
) : PokemonRepository {
    override fun save(pokemon: Pokemon): Pokemon {
        val entity = mapper.toEntity(pokemon)
        val saved = springDataPokemonRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun findById(id: String): Pokemon? {
        val entity = springDataPokemonRepository.findById(id).orElse(null)
        return entity?.let { mapper.toDomain(it) }
    }

    override fun findAll(page: Int, size: Int): com.pokedex.bff.domain.shared.Page<Pokemon> {
        val pageable = PageRequest.of(page, size)
        val pageResult = springDataPokemonRepository.findAll(pageable)
        val content = pageResult.content.map { mapper.toDomain(it) }
        return com.pokedex.bff.domain.shared.Page(
            content = content,
            pageNumber = pageResult.number,
            pageSize = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            isFirst = pageResult.isFirst,
            isLast = pageResult.isLast,
            hasNext = pageResult.hasNext(),
            hasPrevious = pageResult.hasPrevious()
        )
    }
}
