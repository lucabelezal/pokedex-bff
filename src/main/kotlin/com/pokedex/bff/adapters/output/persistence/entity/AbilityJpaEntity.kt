package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "abilities")
data class AbilityJpaEntity(
    @Id
    val id: Long,
    
    @Column(nullable = false, unique = true)
    val name: String,
    
    @Column(columnDefinition = "TEXT")
    val description: String?,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduced_generation_id")
    val introducedGeneration: GenerationJpaEntity?
)
