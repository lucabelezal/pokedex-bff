package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "generations")
data class GenerationJpaEntity(
    @Id
    val id: Long,
    
    @Column(nullable = false, unique = true)
    val name: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    val region: RegionJpaEntity?
)
