package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.StatsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StatsRepository : JpaRepository<StatsEntity, Long>