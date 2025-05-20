package com.pokedex.bff.models

import jakarta.persistence.*

@Entity
@Table(name = "types")
data class Type(
    @Id
    val id: Int,

    @Column(nullable = false)
    val identifier: String,

    @Column(name = "generation_id", nullable = false)
    val generationId: Int,

    @Column(name = "damage_class_id")
    val damageClassId: Int? = null
)
