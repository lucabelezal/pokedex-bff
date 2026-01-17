package com.pokedex.bff.domain.pokemon.entities

/**
 * Representa a relação entre um Pokémon e uma Habilidade.
 * 
 * Nota: Não mantém referência ao Pokemon para evitar ciclos circulares.
 * O Pokemon já contém `abilities: Set<PokemonAbility>`, então a relação
 * bidirecional é desnecessária e problemática para serialização.
 */
data class PokemonAbility(
    val id: Long?,
    val ability: Ability,
    val isHidden: Boolean
)