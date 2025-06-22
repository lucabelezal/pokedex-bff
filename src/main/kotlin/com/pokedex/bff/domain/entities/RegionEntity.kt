package com.pokedex.bff.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "regions")
data class RegionEntity(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = ""
)