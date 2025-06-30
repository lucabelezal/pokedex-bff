package com.pokedex.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "pokemons")
data class Pokemon(
    @Id
    val id: String,
    val name: String,
    val height: Int,
    val weight: Int,
    // TODO: Add types, stats, and other fields based on schema.sql
    @Column(name = "gender_male")
    val genderMale: Float?,
    @Column(name = "gender_female")
    val genderFemale: Float?
) {
    // Default constructor for JPA
    constructor() : this("", "", 0, 0, null, null)
}
