package com.pokedex.bff.adapters.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "species")
data class SpeciesJpaEntity(
    @Id
    val id: Long,
    
    @Column(name = "pokemon_number", length = 10)
    val pokemonNumber: String?,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(name = "species_en")
    val speciesEn: String?,
    
    @Column(name = "species_pt")
    val speciesPt: String?
)
