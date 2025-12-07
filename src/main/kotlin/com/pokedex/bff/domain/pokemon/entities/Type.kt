package com.pokedex.bff.domain.pokemon.entities

data class Type(
    val id: Long,
    val name: String,
    val color: String?
) {
    init {
        require(name.isNotBlank()) { "Type name cannot be blank" }
    }

    /**
     * Verifica se é um tipo especial (Psychic, Dragon, Ghost, Dark, Fairy)
     */
    fun isSpecialType(): Boolean = name.lowercase() in listOf("psychic", "dragon", "ghost", "dark", "fairy")

    /**
     * Verifica se é um tipo físico (Normal, Fighting, Flying, Poison, Ground, Rock, Bug, Steel)
     */
    fun isPhysicalType(): Boolean = name.lowercase() in listOf(
        "normal", "fighting", "flying", "poison", "ground", "rock", "bug", "steel"
    )

    companion object {
        // Constantes para tipos comuns
        const val NORMAL = "Normal"
        const val FIRE = "Fire"
        const val WATER = "Water"
        const val ELECTRIC = "Electric"
        const val GRASS = "Grass"
        const val ICE = "Ice"
        const val FIGHTING = "Fighting"
        const val POISON = "Poison"
        const val GROUND = "Ground"
        const val FLYING = "Flying"
        const val PSYCHIC = "Psychic"
        const val BUG = "Bug"
        const val ROCK = "Rock"
        const val GHOST = "Ghost"
        const val DRAGON = "Dragon"
        const val DARK = "Dark"
        const val STEEL = "Steel"
        const val FAIRY = "Fairy"
    }
}