package com.pokedex.bff.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "regions")
data class Region(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = ""
)

@Entity
@Table(name = "types")
data class Type(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = "",

    @Column(name = "color")
    var color: String? = null
)

@Entity
@Table(name = "egg_groups")
data class EggGroup(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = ""
)

@Entity
@Table(name = "generations")
data class Generation(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    var region: Region? = null
)

@Entity
@Table(name = "abilities")
data class Ability(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "name", unique = true)
    var name: String = "",

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduced_generation_id")
    var introducedGeneration: Generation? = null
)

@Entity
@Table(name = "species")
data class Species(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "pokemon_number")
    var pokemon_number: String? = null,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "species_en")
    var speciesEn: String? = null,

    @Column(name = "species_pt")
    var speciesPt: String? = null
)

@Entity
@Table(name = "stats")
data class Stats(
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

@Entity
@Table(name = "evolution_chains")
data class EvolutionChain(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chain_data", columnDefinition = "jsonb")
    var chainData: String? = null
)

@Entity
@Table(name = "pokemons")
data class Pokemon(
    @Id
    @Column(name = "id")
    var id: Long = 0,

    @Column(name = "number")
    var number: String? = null,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "height")
    var height: Double? = null,

    @Column(name = "weight")
    var weight: Double? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sprites", columnDefinition = "jsonb")
    var sprites: Sprites? = null,

    @Column(name = "gender_rate_value")
    var genderRateValue: Int? = null,

    @Column(name = "egg_cycles")
    var eggCycles: Int? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stats_id", referencedColumnName = "id")
    var stats: Stats? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id")
    var generation: Generation? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    var species: Species? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    var region: Region? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evolution_chain_id")
    var evolutionChain: EvolutionChain? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "pokemon_types",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "type_id")]
    )
    var types: MutableSet<Type> = mutableSetOf(),

    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    var abilities: MutableSet<PokemonAbility> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "pokemon_egg_groups",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "egg_group_id")]
    )
    var eggGroups: MutableSet<EggGroup> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "pokemon_weaknesses",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "type_id")]
    )
    var weaknesses: MutableSet<Type> = mutableSetOf()
)

@Entity
@Table(name = "pokemon_abilities")
data class PokemonAbility(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id")
    var pokemon: Pokemon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id")
    var ability: Ability,

    @Column(name = "is_hidden")
    var isHidden: Boolean = false
)

data class Sprites(
    @JsonProperty("back_default")
    val backDefault: String? = null,
    @JsonProperty("back_female")
    val backFemale: String? = null,
    @JsonProperty("back_shiny")
    val backShiny: String? = null,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String? = null,
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null,
    @JsonProperty("front_shiny")
    val frontShiny: String? = null,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String? = null,
    @JsonProperty("other")
    val other: OtherSprites? = null
)

data class OtherSprites(
    @JsonProperty("dream_world")
    val dreamWorld: DreamWorldSprites? = null,
    @JsonProperty("home")
    val home: HomeSprites? = null,
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkSprites? = null,
    @JsonProperty("showdown")
    val showdown: ShowdownSprites? = null
)

data class DreamWorldSprites(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null
)

data class HomeSprites(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null,
    @JsonProperty("front_shiny")
    val frontShiny: String? = null,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String? = null
)

data class OfficialArtworkSprites(
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_shiny")
    val frontShiny: String? = null
)

data class ShowdownSprites(
    @JsonProperty("back_default")
    val backDefault: String? = null,
    @JsonProperty("back_female")
    val backFemale: String? = null,
    @JsonProperty("back_shiny")
    val backShiny: String? = null,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String? = null,
    @JsonProperty("front_default")
    val frontDefault: String? = null,
    @JsonProperty("front_female")
    val frontFemale: String? = null,
    @JsonProperty("front_shiny")
    val frontShiny: String? = null,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String? = null
)