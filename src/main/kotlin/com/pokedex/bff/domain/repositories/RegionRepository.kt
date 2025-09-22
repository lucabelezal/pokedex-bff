package com.pokedex.bff.domain.repositories

import com.pokedex.bff.infrastructure.persistence.entities.RegionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RegionRepository : JpaRepository<RegionEntity, Long>







