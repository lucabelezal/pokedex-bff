package com.pokedex.bff.domain.pokemon.repository

import com.pokedex.bff.domain.pokemon.entities.PokemonAbility

interface PokemonAbilityRepository {
    fun findById(id: Long): PokemonAbility?
    fun save(pokemonAbility: PokemonAbility): PokemonAbility
    fun findAll(): List<PokemonAbility>
}
