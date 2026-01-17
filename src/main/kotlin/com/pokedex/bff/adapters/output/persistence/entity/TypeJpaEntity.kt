package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.*



@Entity
@Table(name = "types")
data class TypeJpaEntity(
    @Id
    val id: Long,
    val name: String,
    val color: String?
)
