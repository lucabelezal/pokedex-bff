package com.pokedex.bff.infrastructure.repository

import com.pokedex.bff.domain.common.Page
import com.pokedex.bff.domain.entities.Pokemon
import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.infrastructure.persistence.repositories.SpringDataPokemonRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class JpaPokemonRepository(
    private val springDataRepository: SpringDataPokemonRepository
) : PokemonRepository {

    override fun findById(id: Long): Pokemon? {
        return springDataRepository.findById(id)
            .map { com.pokedex.bff.adapters.gateways.PokemonMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAll(): List<Pokemon> {
        return springDataRepository.findAll().map { com.pokedex.bff.adapters.gateways.PokemonMapper.toDomain(it) }
    }

    override fun findAll(page: Int, size: Int): Page<Pokemon> {
        val pageable = PageRequest.of(page, size)
    val springPage = springDataRepository.findAll(pageable)
        
        return Page(
            content = springPage.content.map { com.pokedex.bff.adapters.gateways.PokemonMapper.toDomain(it) },
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
        val entity = com.pokedex.bff.adapters.gateways.toEntityPartial(pokemon)
        val savedEntity = springDataRepository.save(entity)
        return com.pokedex.bff.adapters.gateways.PokemonMapper.toDomain(savedEntity)
    }

    override fun existsById(id: Long): Boolean {
        return springDataRepository.existsById(id)
    }

    override fun deleteById(id: Long) {
        springDataRepository.deleteById(id)
    }
}
