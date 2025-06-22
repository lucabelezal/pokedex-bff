package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.GenerationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GenerationRepository : JpaRepository<GenerationEntity, Long>