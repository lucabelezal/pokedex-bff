package com.pokedex.bff.adapters.output.persistence.entity


import jakarta.persistence.*

@Entity
@Table(name = "pokemons")
data class PokemonJpaEntity(
    @Id
    val id: Long,
    val name: String,
    @ManyToMany
    @JoinTable(
        name = "pokemon_types",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "type_id")]
    )
    val types: Set<TypeJpaEntity> = emptySet()
)
