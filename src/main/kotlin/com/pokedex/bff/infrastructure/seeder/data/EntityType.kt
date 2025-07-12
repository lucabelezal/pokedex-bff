package com.pokedex.bff.infrastructure.seeder.data

enum class EntityType(val entityName: String) {
    REGION("Regiões"),
    TYPE("Tipos"),
    EGG_GROUP("Grupos de Ovos"),
    GENERATION("Gerações"),
    ABILITY("Habilidades"),
    SPECIES("Espécies"),
    STATS("Estatísticas"),
    EVOLUTION_CHAIN("Cadeias de Evolução"),
    POKEMON("Pokémons"),
    WEAKNESS("Fraquezas");
}