package com.pokedex.bff.domain.pokedex.repositories

import com.pokedex.bff.infrastructure.persistence.entities.EvolutionChainEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EvolutionChainRepository : JpaRepository<EvolutionChainEntity, Long>