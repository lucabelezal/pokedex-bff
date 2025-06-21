package com.pokedex.bff.domain.pokedex.repositories

import com.pokedex.bff.infrastructure.persistence.entities.StatsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StatsRepository : JpaRepository<StatsEntity, Long>