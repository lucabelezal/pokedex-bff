package com.pokedex.bff.domain.entities

// Versão pura da entidade Pokemon, sem anotações JPA/framework

data class Pokemon(
    val id: Long,
    val number: String?,
    val name: String,
    val height: Double?,
    val weight: Double?,
    val description: String?,
    val sprites: Sprites?,
    val genderRateValue: Int?,
    val genderMale: Float?,
    val genderFemale: Float?,
    val eggCycles: Int?,
    val stats: Stats?,
    val generation: Generation?,
    val species: Species?,
    val region: Region?,
    val evolutionChain: EvolutionChain?,
    val types: Set<Type>,
    val abilities: Set<PokemonAbility>,
    val eggGroups: Set<EggGroup>,
    val weaknesses: Set<Type>
)
