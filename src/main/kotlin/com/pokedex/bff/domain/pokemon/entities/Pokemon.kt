package com.pokedex.bff.domain.pokemon.entities

import com.pokedex.bff.domain.pokemon.exception.InvalidPokemonException

// Pure Pokemon entity version, without JPA/framework annotations

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
) {
    init {
        require(name.isNotBlank()) { "Pokemon name cannot be blank" }
        require(types.isNotEmpty()) { "Pokemon must have at least one type" }
        require(types.size <= 2) { "Pokemon cannot have more than 2 types" }
        height?.let { require(it > 0) { "Height must be positive" } }
        weight?.let { require(it > 0) { "Weight must be positive" } }
    }

    /**
     * Verifica se o Pokémon é lendário baseado no ID (Gen 1: 144-151)
     */
    fun isLegendary(): Boolean = id in 144..151 || id in 243..251

    /**
     * Formata o número do Pokémon com padding de zeros (ex: 001, 025, 150)
     */
    fun formatNumber(): String = number?.padStart(3, '0') ?: id.toString().padStart(3, '0')

    /**
     * Verifica se o Pokémon possui um tipo específico
     */
    fun hasType(type: Type): Boolean = types.contains(type)

    /**
     * Verifica se o Pokémon pode evoluir
     */
    fun canEvolve(): Boolean = evolutionChain != null

    /**
     * Calcula a efetividade de tipo contra outro Pokémon
     * Retorna multiplicador: 2.0 (super efetivo), 1.0 (normal), 0.5 (pouco efetivo), 0.0 (sem efeito)
     */
    fun calculateTypeEffectiveness(opponent: Pokemon): Double {
        // Simplificação: verificar se algum tipo do atacante é efetivo contra os tipos do oponente
        // TODO: Implementar matriz completa de efetividade de tipos
        return 1.0
    }

    /**
     * Verifica se as estatísticas são balanceadas (diferença entre maior e menor stat < 50)
     */
    fun hasBalancedStats(): Boolean {
        stats?.let {
            val statValues = listOf(it.hp, it.attack, it.defense, it.spAtk, it.spDef, it.speed)
            val max = statValues.maxOrNull() ?: 0
            val min = statValues.minOrNull() ?: 0
            return max - min < 50
        }
        return false
    }
}
