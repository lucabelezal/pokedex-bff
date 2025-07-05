package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.TypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TypeRepository : JpaRepository<TypeEntity, Long>