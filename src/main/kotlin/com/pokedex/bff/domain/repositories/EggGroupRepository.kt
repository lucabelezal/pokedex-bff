package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.EggGroupEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EggGroupRepository : JpaRepository<EggGroupEntity, Long>