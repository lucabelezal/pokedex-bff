package com.pokedex.bff.utils

enum class JsonFile(val filePath: String) {
    REGIONS("data/01_region.json"),
    TYPES("data/02_type.json"),
    EGG_GROUPS("data/03_egg_group.json"),
    GENERATIONS("data/04_generation.json"),
    ABILITIES("data/05_ability.json"),
    SPECIES("data/06_species.json"),
    STATS("data/07_stats.json"),
    EVOLUTION_CHAINS("data/08_evolution_chains.json"),
    WEAKNESSES("data/09_weaknesses.json"),
    POKEMONS("data/10_pokemon.json");

    companion object {
        fun getPath(file: JsonFile): String {
            return file.filePath
        }
    }
}