package com.pokedex.bff.infrastructure.persistence.repositories

import com.pokedex.bff.infrastructure.persistence.entities.PokemonAbilityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataPokemonAbilityRepository : JpaRepository<PokemonAbilityEntity, Long> {
    fun findByPokemonEntityId(pokemonId: Long): List<PokemonAbilityEntity>
}