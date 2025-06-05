package com.pokedex.bff.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode // Consider using JsonNode for JSONB
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import java.io.Serializable
import java.math.BigDecimal
// import org.hibernate.annotations.Type // Needed for proper JSONB mapping with hibernate-types

// --- Entidades Core ---

@Entity
@Table(name = "Region")
data class Region(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    // Optional: Add a back-reference to Generations if needed for navigation
    @JsonIgnore
    @OneToMany(mappedBy = "region", cascade = [CascadeType.ALL])
    val generations: MutableSet<Generation> = mutableSetOf()
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
    val color: String?,

    // Optional: Add back-references if needed for navigation
    @JsonIgnore
    @OneToMany(mappedBy = "type", cascade = [CascadeType.ALL])
    val pokemonTypes: MutableSet<PokemonType> = mutableSetOf(),

    @JsonIgnore
    @OneToMany(mappedBy = "weaknessType", cascade = [CascadeType.ALL])
    val pokemonWeaknesses: MutableSet<PokemonWeakness> = mutableSetOf()
)

@Entity
@Table(name = "Egg_Group")
data class EggGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    // Optional: Add a back-reference if needed for navigation
    @JsonIgnore
    @OneToMany(mappedBy = "eggGroup", cascade = [CascadeType.ALL])
    val pokemonEggGroups: MutableSet<PokemonEggGroup> = mutableSetOf()
)


@Entity
@Table(name = "Species")
data class Species(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    // national_pokedex_number is unique per Species, not per Pokemon form
    @Column(name = "national_pokedex_number", nullable = false, unique = true, length = 4)
    val nationalPokedexNumber: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "species_en")
    val speciesEn: String?,

    @Column(name = "species_pt")
    val speciesPt: String?,

    // Optional: Add a back-reference to Pokemon forms belonging to this species
    @JsonIgnore
    @OneToMany(mappedBy = "species", cascade = [CascadeType.ALL])
    val pokemonForms: MutableSet<Pokemon> = mutableSetOf()
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
    val region: Region,

    // Optional: Add back-references if needed for navigation
    @JsonIgnore
    @OneToMany(mappedBy = "generation", cascade = [CascadeType.ALL])
    val pokemon: MutableSet<Pokemon> = mutableSetOf(),

    @JsonIgnore
    @OneToMany(mappedBy = "introducedGeneration", cascade = [CascadeType.ALL])
    val abilities: MutableSet<Ability> = mutableSetOf()
)

@Entity
@Table(name = "Ability")
data class Ability(
    @Id // Use ID as PK now
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "name", nullable = false, unique = true) // Name is unique
    val name: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introduced_generation_id")
    val introducedGeneration: Generation?,

    // Optional: Add a back-reference if needed for navigation
    @JsonIgnore
    @OneToMany(mappedBy = "ability", cascade = [CascadeType.ALL])
    val pokemonAbilities: MutableSet<PokemonAbility> = mutableSetOf()
)

@Entity
@Table(name = "Stats")
// Stats should have a 1:1 relationship with Pokemon
// The PK of Stats is also the FK to Pokemon.id
data class Stats(
    @Id // pokemon_id is the PK
    @Column(name = "pokemon_id", nullable = false)
    val pokemonId: Int,

    @MapsId // Maps this primary key to the primary key of the associated Pokemon entity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id") // Join column is the PK column itself
    val pokemon: Pokemon, // Back-reference to the Pokemon this stat block belongs to

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

    // national_pokedex_number is NOT unique for Pokemon forms (e.g., Mega Evolutions)
    // It is unique in the Species table, and Pokemon.species_id links to Species.
    @Column(name = "national_pokedex_number", nullable = false, length = 4)
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

    // Mapping JSONB as String requires manual JSON processing or a custom type converter
    // Using Jackson's JsonNode is another option with proper type mapping
    @Column(name = "sprites", columnDefinition = "JSONB")
    // @Type(type = "jsonb") // Example annotation if using hibernate-types
    val sprites: String?, // Or JsonNode? if using Jackson type converter

    @Column(name = "gender_rate_value")
    val genderRateValue: Int?,

    @Column(name = "egg_cycles")
    val eggCycles: Int?,

    // OneToOne relationship with Stats is now managed from the Stats side
    // You can add a back-reference here if needed, though not strictly required for the 1:1 PK/FK setup
    @OneToOne(mappedBy = "pokemon", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val stats: Stats? = null,

    // Relationships with junction tables (already good with @JsonIgnore)
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
    val pokemonWeaknesses: MutableSet<PokemonWeakness> = mutableSetOf(),

    // Relationships for evolution links
    @JsonIgnore
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val evolutionLinksAsOrigin: MutableSet<EvolutionLink> = mutableSetOf(),

    @JsonIgnore
    @OneToMany(mappedBy = "targetPokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val evolutionLinksAsTarget: MutableSet<EvolutionLink> = mutableSetOf()
)


// --- Classes para Chaves Compostas ---

// Composite key for Pokemon_Type
data class PokemonTypeId(
    var pokemon: Int? = null, // Refers to Pokemon.id
    var type: Int? = null    // Refers to Type.id
) : Serializable

// Composite key for Pokemon_Ability
data class PokemonAbilityId(
    var pokemon: Int? = null, // Refers to Pokemon.id
    var ability: Int? = null // Refers to Ability.id
) : Serializable

// Composite key for Pokemon_Egg_Group
data class PokemonEggGroupId(
    var pokemon: Int? = null, // Refers to Pokemon.id
    var eggGroup: Int? = null // Refers to EggGroup.id
) : Serializable

// Composite key for Pokemon_Weakness
data class PokemonWeaknessId(
    var pokemon: Int? = null, // Refers to Pokemon.id
    var weaknessType: Int? = null // Refers to Type.id
) : Serializable

// EvolutionLinkPk class is already provided and correct for Evolution_Link's composite key
data class EvolutionLinkPk(
    var evolutionChain: Int? = null, // Refers to EvolutionChain.id
    var pokemon: Int? = null // Refers to Pokemon.id
) : Serializable


// --- Tabelas de Junção (usando @IdClass para Chaves Compostas) ---

@Entity
@Table(name = "Pokemon_Type")
@IdClass(PokemonTypeId::class) // Use the composite key class
data class PokemonType(
    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    val type: Type
) {
    // JPA requires a no-arg constructor for IdClass, often handled by Kotlin's default constructor
    // but for safety/legacy JPA providers, explicit secondary constructor or using Kotlin's
    // `allOpen` plugin might be needed. Data classes work well with default constructors.
}


@Entity
@Table(name = "Pokemon_Ability")
@IdClass(PokemonAbilityId::class) // Use the composite key class
data class PokemonAbility(
    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id", nullable = false, referencedColumnName = "id") // Reference Ability.id
    val ability: Ability,

    @Column(name = "is_hidden")
    val isHidden: Boolean?
)


@Entity
@Table(name = "Pokemon_Egg_Group")
@IdClass(PokemonEggGroupId::class) // Use the composite key class
data class PokemonEggGroup(
    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egg_group_id", nullable = false)
    val eggGroup: EggGroup
)


@Entity
@Table(name = "Pokemon_Weakness")
@IdClass(PokemonWeaknessId::class) // Use the composite key class
data class PokemonWeakness(
    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Id // Part of the composite key
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
@IdClass(EvolutionLinkPk::class) // Use the composite key class
data class EvolutionLink(
    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evolution_chain_id", nullable = false)
    val evolutionChain: EvolutionChain,

    @Id // Part of the composite key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon, // The Pokémon that evolves

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_pokemon_id")
    val targetPokemon: Pokemon?, // The Pokémon it evolves into (nullable)

    // Map the JSONB column for the condition
    // Requires a custom type converter or library for proper JSONB handling
    @Column(name = "condition", columnDefinition = "JSONB")
    // @Type(type = "jsonb") // Example if using hibernate-types
    val condition: String? // Or JsonNode? if using Jackson type converter
) {
    // JPA requires a no-arg constructor for IdClass, often handled by Kotlin's default constructor
    // Or use the `allOpen` plugin
}

// EvolutionLinkPk is already provided and correct for the composite key
// data class EvolutionLinkPk(...) : Serializable