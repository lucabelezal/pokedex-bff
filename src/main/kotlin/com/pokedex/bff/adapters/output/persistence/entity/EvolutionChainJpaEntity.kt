package com.pokedex.bff.adapters.output.persistence.entity

import com.pokedex.bff.adapters.output.persistence.converter.EvolutionChainDataConverter
import com.pokedex.bff.domain.pokemon.entities.EvolutionChainData
import jakarta.persistence.*

@Entity
@Table(name = "evolution_chains")
data class EvolutionChainJpaEntity(
    @Id
    val id: Long,
    
    @Column(name = "chain_data", columnDefinition = "JSONB", nullable = false)
    @Convert(converter = EvolutionChainDataConverter::class)
    val chainData: EvolutionChainData?
)
