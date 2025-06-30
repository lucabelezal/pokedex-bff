package com.pokedex.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "types") // Assuming a 'types' table from the schema.sql
data class PokemonType(
    @Id
    val id: Long? = null, // Assuming id is generated or comes from JSON
    val name: String
) {
    // Default constructor for JPA
    constructor() : this(null, "")
}
