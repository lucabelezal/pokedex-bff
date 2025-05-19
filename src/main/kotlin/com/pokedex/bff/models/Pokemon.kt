package com.pokedex.bff.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "pokemon")
data class Pokemon(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "species_id")
    val speciesId: Long?,

    @Column(name = "height")
    val height: Int?,

    @Column(name = "weight")
    val weight: Int?,

    @Column(name = "base_experience")
    val baseExperience: Int?,

    @Column(name = "order_number")
    val order: Int?,

    @Column(name = "is_default")
    val isDefault: Boolean
)
