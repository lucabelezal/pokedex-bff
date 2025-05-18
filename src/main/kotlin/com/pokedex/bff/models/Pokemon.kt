package com.pokedex.bff.models

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Pokemon(
    @Id
    val id: Long,
    val identifier: String,
    val speciesId: Long?,
    val height: Int?,
    val weight: Int?,
    val baseExperience: Int?,
    val order: Int?,
    val isDefault: Boolean
)