package com.pokedex.bff.domain.entities

data class PokemonAbility(
    val id: Long?,
    val pokemon: Pokemon,
    val ability: Ability,
    val isHidden: Boolean
)
