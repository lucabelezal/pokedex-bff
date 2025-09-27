package com.pokedex.bff.infrastructure.persistence.repositories

import com.pokedex.bff.infrastructure.persistence.entities.EggGroupEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataEggGroupRepository : JpaRepository<EggGroupEntity, Long>
