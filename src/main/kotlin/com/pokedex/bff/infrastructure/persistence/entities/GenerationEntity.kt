package com.pokedex.bff.infrastructure.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "generations")
data class GenerationEntity(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    var region: RegionEntity? = null
)
