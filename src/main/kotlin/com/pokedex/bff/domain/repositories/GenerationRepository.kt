package com.pokedex.bff.domain.repositories

import com.pokedex.bff.infrastructure.persistence.entities.GenerationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GenerationRepository : JpaRepository<GenerationEntity, Long>