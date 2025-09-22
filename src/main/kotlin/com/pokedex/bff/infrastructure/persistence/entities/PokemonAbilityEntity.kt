package com.pokedex.bff.infrastructure.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "pokemon_abilities")
data class PokemonAbilityEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id")
    var pokemon: PokemonEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id")
    var ability: AbilityEntity,

    @Column(name = "is_hidden")
    var isHidden: Boolean = false
)