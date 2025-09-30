package com.pokedex.bff.domain.pokemon.entities

data class PokemonAbility(
    val id: Long?,
    val pokemon: Pokemon,
    val ability: Ability,
    val isHidden: Boolean
)