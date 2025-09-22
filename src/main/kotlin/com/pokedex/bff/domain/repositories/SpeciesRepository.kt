package com.pokedex.bff.domain.repositories

import com.pokedex.bff.infrastructure.persistence.entities.SpeciesEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpeciesRepository : JpaRepository<SpeciesEntity, Long>