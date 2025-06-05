package com.pokedex.bff.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal

// --- Entidades Core ---

@Entity
@Table(name = "Region")
data class Region(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String
)

@Entity
@Table(name = "Type")
data class Type(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    @Column(name = "color")
    val color: String?
)

@Entity
@Table(name = "Egg_Group")
data class EggGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String
)

@Entity
@Table(name = "Species")
data class Species(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "national_pokedex_number", nullable = false, unique = true, length = 4)
    val nationalPokedexNumber: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "species_en")
    val speciesEn: String?,

    @Column(name = "species_pt")
    val speciesPt: String?
)

@Entity
@Table(name = "Generation")
data class Generation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    val region: Region
)

@Entity
@Table(name = "Ability")
data class Ability(
    @Id
    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduced_generation_id")
    val introducedGeneration: Generation?
)

@Entity
@Table(name = "Stats")
data class Stats(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "pokemon_national_pokedex_number", nullable = false, unique = true, length = 4)
    val pokemonNationalPokedexNumber: String,

    @Column(name = "total")
    val total: Int?,

    @Column(name = "hp")
    val hp: Int?,

    @Column(name = "attack")
    val attack: Int?,

    @Column(name = "defense")
    val defense: Int?,

    @Column(name = "sp_atk")
    val spAtk: Int?,

    @Column(name = "sp_def")
    val spDef: Int?,

    @Column(name = "speed")
    val speed: Int?
)

@Entity
@Table(name = "Pokemon")
data class Pokemon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "national_pokedex_number", nullable = false, unique = true, length = 4)
    val nationalPokedexNumber: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id", nullable = false)
    val generation: Generation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    val species: Species,

    @Column(name = "height_m", precision = 4, scale = 2)
    val heightM: BigDecimal?,

    @Column(name = "weight_kg", precision = 6, scale = 2)
    val weightKg: BigDecimal?,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String?,

    @Column(name = "sprites", columnDefinition = "JSONB")
    val sprites: String?,

    @Column(name = "gender_rate_value")
    val genderRateValue: Int?,

    @Column(name = "egg_cycles")
    val eggCycles: Int?,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stats_id", referencedColumnName = "id")
    val stats: Stats?,

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
    val pokemonWeaknesses: MutableSet<PokemonWeakness> = mutableSetOf()
)


// --- Tabelas de Junção para Many-to-Many com atributos extras ou chaves compostas ---

@Entity
@Table(name = "Pokemon_Type")
data class PokemonType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    val type: Type
)

@Entity
@Table(name = "Pokemon_Ability")
data class PokemonAbility(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_name", nullable = false, referencedColumnName = "name")
    val ability: Ability,

    @Column(name = "is_hidden")
    val isHidden: Boolean?
)

@Entity
@Table(name = "Pokemon_Egg_Group")
data class PokemonEggGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egg_group_id", nullable = false)
    val eggGroup: EggGroup
)

@Entity
@Table(name = "Pokemon_Weakness")
data class PokemonWeakness(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weakness_type_id", nullable = false)
    val weaknessType: Type
)


// --- Entidades de Evolução ---

@Entity
@Table(name = "Evolution_Chain")
data class EvolutionChain(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @JsonIgnore
    @OneToMany(mappedBy = "evolutionChain", cascade = [CascadeType.ALL], orphanRemoval = true)
    val evolutionLinks: MutableSet<EvolutionLink> = mutableSetOf()
)

@Entity
@Table(name = "Evolution_Link")
@IdClass(EvolutionLinkPk::class)
data class EvolutionLink(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evolution_chain_id", nullable = false)
    val evolutionChain: EvolutionChain,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_pokemon_id")
    val targetPokemon: Pokemon?,

    @Column(name = "condition_type")
    val conditionType: String?,

    @Column(name = "condition_value")
    val conditionValue: String?
)

data class EvolutionLinkPk(
    val evolutionChain: Int? = null,
    val pokemon: Int? = null
) : java.io.Serializable
