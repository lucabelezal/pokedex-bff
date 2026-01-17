package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "regions")
data class RegionJpaEntity(
    @Id
    val id: Long,
    
    @Column(nullable = false, unique = true)
    val name: String
)
