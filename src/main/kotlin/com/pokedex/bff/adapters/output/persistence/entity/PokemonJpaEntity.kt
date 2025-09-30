package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "pokemon")
data class PokemonJpaEntity(
    @Id
    val id: String,
    val name: String,
    val type: String,
    val level: Int
)
