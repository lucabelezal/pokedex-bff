package com.pokedex.bff.domain.model

data class PokemonAbility(
    val id: Long?,
    val pokemon: Pokemon,
    val ability: Ability,
    val isHidden: Boolean
)
