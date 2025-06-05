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
    val species_en: String?,
    val species_pt: String?
)

@Entity
@Table(name = "Generation")
data class Generation(
    @Id
    val id: Int,
    @Column(nullable = false)
    val name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regiaoId")
    val region: Region?
)

@Entity
@Table(name = "Ability")
data class Ability(
    @Id
    val id: Int,
    @Column(nullable = false)
    val name: String,
    @Column(columnDefinition = "TEXT")
    val description: String?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introducedGenerationId")
    val introducedGeneration: Generation?
)

@Entity
@Table(name = "Stats")
data class Stats(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID agora é auto-gerado pelo DB
    val id: Int = 0, // Valor padrão para auto-gerado
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
    val heightM: Double?,
    val weightKg: Double?,
    @Column(columnDefinition = "TEXT")
    val description: String?,
    @Column(columnDefinition = "jsonb") // Armazena sprites como JSONB
    val sprites: String?, // String JSON para armazenar o mapa de sprites
    val gender_rate_value: Int?, // 0-8 for female%
    val eggCycles: Int?,
    @OneToOne(fetch = FetchType.LAZY) // Relacionamento 1-para-1 com Stats
    @JoinColumn(name = "stats_id") // Coluna de junção para Stats
    val stats: Stats?,
    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento com EvolutionChain
    @JoinColumn(name = "evolution_chain_id")
    val evolutionChain: EvolutionChain? = null, // Pode ser nulo
    @JsonIgnore // Evita serialização recursiva
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pokemonTypes: MutableSet<PokemonType> = mutableSetOf(),
    @JsonIgnore // Evita serialização recursiva
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pokemonAbilities: MutableSet<PokemonAbility> = mutableSetOf(),
    @JsonIgnore // Evita serialização recursiva
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pokemonEggGroups: MutableSet<PokemonEggGroup> = mutableSetOf(),
    @JsonIgnore // Evita serialização recursiva
    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    val weaknesses: MutableSet<Weakness> = mutableSetOf()
) : java.io.Serializable

// ID Class for composite key of PokemonType
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

// ID Class for composite key of PokemonAbility
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

    val isHidden: Boolean
) : java.io.Serializable

// ID Class for composite key of PokemonEggGroup
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming Weakness has its own ID now
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    val pokemon: Pokemon,

    @Column(nullable = false)
    val pokemon_name: String, // Keep for convenience, but can be derived from Pokemon entity

    @Column(nullable = false)
    val weakness_type: String
)

@Entity
@Table(name = "Evolution_Chain")
data class EvolutionChain(
    @Id
    val id: Int, // ID da cadeia de evolução é definido por JSON
    @JsonIgnore // Evita serialização recursiva
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
    val pokemon: Pokemon, // Pokémon de origem

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetPokemonId")
    val targetPokemon: Pokemon?, // Pokémon alvo (pode ser nulo para o último da cadeia)

    @Column // Coluna para o nome do Pokémon alvo (para conveniência)
    val targetPokemonName: String?,

    @Column(nullable = true) // Tipo da condição de evolução (e.g., "level-up", "trade")
    val condition_type: String?,

    @Column(columnDefinition = "jsonb") // Detalhes da condição como JSONB (e.g., min_level, item)
    val condition_value: String?
)