package com.pokedex.bff.infrastructure.persistence.repositories

import com.pokedex.bff.infrastructure.persistence.entities.RegionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataRegionRepository : JpaRepository<RegionEntity, Long>