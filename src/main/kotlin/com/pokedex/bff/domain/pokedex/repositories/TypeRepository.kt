package com.pokedex.bff.domain.pokedex.repositories

import com.pokedex.bff.infrastructure.persistence.entities.TypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TypeRepository : JpaRepository<TypeEntity, Long> {
    fun findByNameIn(names: List<String>): List<TypeEntity>
}