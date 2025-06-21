package com.pokedex.bff.domain.pokedex.repositories

import com.pokedex.bff.infrastructure.persistence.entities.AbilityEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AbilityRepository : JpaRepository<AbilityEntity, Long>