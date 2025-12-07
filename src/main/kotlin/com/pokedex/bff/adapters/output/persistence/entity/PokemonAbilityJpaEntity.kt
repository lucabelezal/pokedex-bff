package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "pokemon_abilities")
data class PokemonAbilityJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: PokemonJpaEntity,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id", nullable = false)
    val ability: AbilityJpaEntity,
    
    @Column(name = "is_hidden", nullable = false)
    val isHidden: Boolean = false
)
