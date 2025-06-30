package com.pokedex.bff.domain.entities

import com.pokedex.bff.application.valueobjects.SpritesVO
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "pokemons")
data class PokemonEntity(
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
    var sprites: SpritesVO? = null,

    @Column(name = "gender_rate_value")
    var genderRateValue: Int? = null,

    @Column(name = "gender_male")
    var genderMale: Float? = null,

    @Column(name = "gender_female")
    var genderFemale: Float? = null,

    @Column(name = "egg_cycles")
    var eggCycles: Int? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stats_id", referencedColumnName = "id")
    var stats: StatsEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id")
    var generation: GenerationEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    var species: SpeciesEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    var region: RegionEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evolution_chain_id")
    var evolutionChain: EvolutionChainEntity? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "pokemon_types",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "type_id")]
    )
    var types: MutableSet<TypeEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "pokemon", cascade = [CascadeType.ALL], orphanRemoval = true)
    var abilities: MutableSet<PokemonAbilityEntity> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "pokemon_egg_groups",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "egg_group_id")]
    )
    var eggGroups: MutableSet<EggGroupEntity> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "pokemon_weaknesses",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "type_id")]
    )
    var weaknesses: MutableSet<TypeEntity> = mutableSetOf()
)