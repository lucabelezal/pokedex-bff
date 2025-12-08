package com.pokedex.bff.adapters.output.persistence.entity

import com.pokedex.bff.adapters.output.persistence.converter.SpritesJsonConverter
import com.pokedex.bff.domain.pokemon.entities.Sprites
import jakarta.persistence.*

@Entity
@Table(name = "pokemons")
data class PokemonJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    
    @Column(length = 10)
    val number: String?,
    
    @Column(nullable = false, length = 50)
    val name: String,
    
    @Column
    val height: Double?,
    
    @Column
    val weight: Double?,
    
    @Column(columnDefinition = "TEXT")
    val description: String?,
    
    @Column(name = "sprites", columnDefinition = "JSONB")
    @Convert(converter = SpritesJsonConverter::class)
    val sprites: Sprites?,
    
    @Column(name = "gender_rate_value")
    val genderRateValue: Int?,
    
    @Column(name = "gender_male")
    val genderMale: Float?,
    
    @Column(name = "gender_female")
    val genderFemale: Float?,
    
    @Column(name = "egg_cycles")
    val eggCycles: Int?,
    
    @Embedded
    val stats: StatsEmbeddable?,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id")
    val generation: GenerationJpaEntity?,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    val species: SpeciesJpaEntity?,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    val region: RegionJpaEntity?,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evolution_chain_id")
    val evolutionChain: EvolutionChainJpaEntity?,
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pokemon_types",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "type_id")]
    )
    val types: Set<TypeJpaEntity> = emptySet(),
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pokemon_egg_groups",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "egg_group_id")]
    )
    val eggGroups: Set<EggGroupJpaEntity> = emptySet(),
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pokemon_weaknesses",
        joinColumns = [JoinColumn(name = "pokemon_id")],
        inverseJoinColumns = [JoinColumn(name = "type_id")]
    )
    val weaknesses: Set<TypeJpaEntity> = emptySet(),
    
    @OneToMany(mappedBy = "pokemon", fetch = FetchType.LAZY)
    val abilities: Set<PokemonAbilityJpaEntity> = emptySet()
)

@Embeddable
data class StatsEmbeddable(
    @Column(name = "stat_total")
    val total: Int,
    
    @Column(name = "stat_hp")
    val hp: Int,
    
    @Column(name = "stat_attack")
    val attack: Int,
    
    @Column(name = "stat_defense")
    val defense: Int,
    
    @Column(name = "stat_sp_atk")
    val spAtk: Int,
    
    @Column(name = "stat_sp_def")
    val spDef: Int,
    
    @Column(name = "stat_speed")
    val speed: Int
)
