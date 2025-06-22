package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.PokemonAbilityEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PokemonAbilityRepository : JpaRepository<PokemonAbilityEntity, Long>