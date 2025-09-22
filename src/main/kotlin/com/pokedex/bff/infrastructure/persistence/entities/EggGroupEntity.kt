package com.pokedex.bff.infrastructure.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "egg_groups")
data class EggGroupEntity(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = ""
)