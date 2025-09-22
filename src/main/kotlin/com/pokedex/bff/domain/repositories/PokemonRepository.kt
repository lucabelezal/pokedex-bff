package com.pokedex.bff.domain.repositories

import com.pokedex.bff.infrastructure.persistence.entities.PokemonEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PokemonRepository : JpaRepository<PokemonEntity, Long> {
    override fun findAll(pageable: Pageable): Page<PokemonEntity>
}