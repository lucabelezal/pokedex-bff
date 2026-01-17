package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "egg_groups")
data class EggGroupJpaEntity(
    @Id
    val id: Long,
    
    @Column(nullable = false, unique = true)
    val name: String
)
