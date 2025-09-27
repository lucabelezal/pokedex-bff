
package com.pokedex.bff.domain.repositories

import com.pokedex.bff.domain.entities.PokemonAbility

interface PokemonAbilityRepository {
    fun findById(id: Long): PokemonAbility?
    fun findAll(): List<PokemonAbility>
    fun findByPokemonId(pokemonId: Long): List<PokemonAbility>
    fun save(pokemonAbility: PokemonAbility): PokemonAbility
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
