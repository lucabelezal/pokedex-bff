package com.pokedex.bff.infrastructure.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "species")
data class SpeciesEntity(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "pokemon_number")
    var pokemonNumber: String? = null,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "species_en")
    var speciesEn: String? = null,

    @Column(name = "species_pt")
    var speciesPt: String? = null
)
