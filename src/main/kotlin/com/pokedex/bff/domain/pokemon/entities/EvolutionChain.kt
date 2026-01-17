package com.pokedex.bff.domain.pokemon.entities

data class EvolutionChain(
    val id: Long,
    val chainData: EvolutionChainData?
)

data class EvolutionChainData(
    val pokemon: PokemonEvolution,
    val evolutionsTo: List<EvolutionStep> = emptyList()
)

data class EvolutionStep(
    val pokemon: PokemonEvolution,
    val condition: EvolutionCondition,
    val evolutionsTo: List<EvolutionStep> = emptyList()
)

data class PokemonEvolution(
    val id: Long,
    val name: String
)

data class EvolutionCondition(
    val type: String,
    val value: Any?,
    val description: String
)