package com.pokedex.bff.infrastructure.persistence.repositories

import com.pokedex.bff.infrastructure.persistence.entities.GenerationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataGenerationRepository : JpaRepository<GenerationEntity, Long>
