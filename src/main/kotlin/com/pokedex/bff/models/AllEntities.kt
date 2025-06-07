package com.pokedex.bff.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "Region")
data class Region(
    @Id
    val id: Int,

    @Column(nullable = false)
    val name: String
)

@Entity
@Table(name = "Type")
data class Type(
    @Id
    val id: Int,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val color: String?
)

@Entity
@Table(name = "Egg_Group")
data class EggGroup(
    @Id
    val id: Int,

    @Column(nullable = false)
    val name: String
)

@Entity
@Table(name = "Species")
data class Species(
    @Id
    val id: Int,

    @Column(nullable = false, unique = true)
    val nationalPokedexNumber: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val speciesEn: String?,

    @Column(nullable = true)
    val speciesPT: String?
)

@Entity
@Table(name = "Generation")
data class Generation(
    @Id
    val id: Int,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regionId")
    val region: Region?
)

@Entity
@Table(name = "Ability")
data class Ability(
    @Id
    val id: Int,

    @Column(nullable = false)
    val name: String,

    @Column(columnDefinition = "TEXT", nullable = true)
    val description: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introducedGenerationId")
    val introducedGeneration: Generation?
)

@Entity
@Table(name = "Stats")
data class Stats(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true)
    val pokemonNationalPokedexNumber: String,

    @Column(nullable = false)
    val pokemonName: String,

    @Column(nullable = false)
    val total: Int,

    @Column(nullable = false)
    val hp: Int,

    @Column(nullable = false)
    val attack: Int,

    @Column(nullable = false)
    val defense: Int,

    @Column(nullable = false)
    val spAtk: Int,

    @Column(nullable = false)
    val spDef: Int,

    @Column(nullable = false)
    val speed: Int
)

@Entity
@Table(name = "Pokemon")
data class Pokemon(
    @Id
    val id: Int,

    @Column(nullable = false, unique = true)
    val nationalPokedexNumber: String,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id", nullable = false)
    val generation: Generation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    val species: Species,

    @Column(nullable = true)
    val height: Double?,

    @Column(nullable = true)
    val weight: Double?,

    @Column(columnDefinition = "TEXT", nullable = true)
    val description: String?,

    @Column(columnDefinition = "jsonb", nullable = true)
    val sprites: String?,

    @Column(nullable = true)
    val gender_rate_value: Int?,

    @Column(nullable = true)
    val eggCycles: Int?,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stats_id")
    val stats: Stats?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evolution_chain_id")
    val evolutionChain: EvolutionChain? = null,

    @JsonIgnore
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pokemonTypes: MutableSet<PokemonType> = mutableSetOf(),

    @JsonIgnore
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pokemonAbilities: MutableSet<PokemonAbility> = mutableSetOf(),

    @JsonIgnore
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pokemonEggGroups: MutableSet<PokemonEggGroup> = mutableSetOf(),

    @JsonIgnore
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val weaknesses: MutableSet<Weakness> = mutableSetOf()
) : java.io.Serializable

@Embeddable
class PokemonTypeId : java.io.Serializable {
    var pokemon: Int? = null
    var type: Int? = null
}

@Entity
@Table(name = "Pokemon_Type")
@IdClass(PokemonTypeId::class)
data class PokemonType(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    val type: Type
) : java.io.Serializable

@Embeddable
class PokemonAbilityId : java.io.Serializable {
    var pokemon: Int? = null
    var ability: Int? = null
}

@Entity
@Table(name = "Pokemon_Ability")
@IdClass(PokemonAbilityId::class)
data class PokemonAbility(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id", nullable = false)
    val ability: Ability,

    @Column(nullable = false)
    val isHidden: Boolean
) : java.io.Serializable

@Embeddable
class PokemonEggGroupId : java.io.Serializable {
    var pokemon: Int? = null
    var eggGroup: Int? = null
}

@Entity
@Table(name = "Pokemon_Egg_Group")
@IdClass(PokemonEggGroupId::class)
data class PokemonEggGroup(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egg_group_id", nullable = false)
    val eggGroup: EggGroup
) : java.io.Serializable

@Entity
@Table(name = "Weakness")
data class Weakness(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Column(nullable = false)
    val pokemon_name: String,

    @Column(nullable = false)
    val weakness_type: String
)

@Entity
@Table(name = "Evolution_Chain")
data class EvolutionChain(
    @Id
    val id: Int,
    @JsonIgnore
    @OneToMany(mappedBy = "evolutionChain", cascade = [CascadeType.ALL], orphanRemoval = true)
    val evolutionDetails: MutableSet<EvolutionDetail> = mutableSetOf()
) : java.io.Serializable

@Entity
@Table(name = "Evolution_Detail")
data class EvolutionDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evolutionChainId", nullable = false)
    val evolutionChain: EvolutionChain,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemonId", nullable = false)
    val pokemon: Pokemon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetPokemonId")
    val targetPokemon: Pokemon?,

    @Column(nullable = true)
    val targetPokemonName: String?,

    @Column(nullable = true)
    val condition_type: String?,

    @Column(columnDefinition = "jsonb", nullable = true)
    val condition_value: String?
)