package com.pokedex.bff.models

import jakarta.persistence.*

@Entity
@Table(name = "regions")
data class Region(
    @Id
    val id: Int,

    @Column(nullable = false)
    val identifier: String
)