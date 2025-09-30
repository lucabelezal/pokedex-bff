package com.pokedex.bff.domain.pokemon.event

data class PokemonLeveledUpEvent(val pokemonId: Long, val newLevel: Int)