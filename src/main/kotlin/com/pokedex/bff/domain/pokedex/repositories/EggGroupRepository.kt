package com.pokedex.bff.domain.pokedex.repositories

import com.pokedex.bff.infrastructure.persistence.entities.EggGroupEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EggGroupRepository : JpaRepository<EggGroupEntity, Long>