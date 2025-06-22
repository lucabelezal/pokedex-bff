package com.pokedex.bff.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "stats")
data class StatsEntity(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "total")
    var total: Int = 0,

    @Column(name = "hp")
    var hp: Int = 0,

    @Column(name = "attack")
    var attack: Int = 0,

    @Column(name = "defense")
    var defense: Int = 0,

    @Column(name = "sp_atk")
    var spAtk: Int = 0,

    @Column(name = "sp_def")
    var spDef: Int = 0,

    @Column(name = "speed")
    var speed: Int = 0
)