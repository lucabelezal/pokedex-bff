package com.pokedex.bff.models

import jakarta.persistence.*
import java.io.Serializable // Necessário para classes @Embeddable

// --- Entidades com Chave Primária Simples (@Id) ---

@Entity
@Table(name = "super_contest_effects")
data class SuperContestEffect(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "appeal")
    val appeal: Int
)

@Entity
@Table(name = "regions")
data class Region(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "generations")
data class Generation(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "main_region_id")
    val mainRegionId: Int?,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "damage_classes")
data class DamageClass(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "types")
data class Type(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "generation_id")
    val generationId: Int,

    @Column(name = "damage_class_id")
    val damageClassId: Int?
)

@Entity
@Table(name = "stats")
data class Stat(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "damage_class_id")
    val damageClassId: Int?,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "is_battle_only")
    val isBattleOnly: Boolean,

    @Column(name = "game_index")
    val gameIndex: Int?
)

@Entity
@Table(name = "growth_rates")
data class GrowthRate(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "formula")
    val formula: String?
)

@Entity
@Table(name = "egg_groups")
data class EggGroup(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "genders")
data class Gender(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "characteristics")
data class Characteristic(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "stat_id")
    val statId: Int,

    @Column(name = "gene_mod_5")
    val geneMod5: Int
)

@Entity
@Table(name = "flavors")
data class Flavor(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "natures")
data class Nature(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "decreased_stat_id")
    val decreasedStatId: Int,

    @Column(name = "increased_stat_id")
    val increasedStatId: Int,

    @Column(name = "hates_flavor_id")
    val hatesFlavorId: Int,

    @Column(name = "likes_flavor_id")
    val likesFlavorId: Int,

    @Column(name = "game_index")
    val gameIndex: Int?
)

@Entity
@Table(name = "move_targets")
data class MoveTarget(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "move_effects")
data class MoveEffect(
    @Id
    @Column(name = "id")
    val id: Int
)

@Entity
@Table(name = "languages")
data class Language(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "iso639")
    val iso639: String,

    @Column(name = "iso3166")
    val iso3166: String,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "official")
    val official: Boolean,

    @Column(name = "display_order")
    val displayOrder: Int?
)

@Entity
@Table(name = "moves")
data class Move(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "generation_id")
    val generationId: Int,

    @Column(name = "type_id")
    val typeId: Int,

    @Column(name = "power")
    val power: Int?,

    @Column(name = "pp")
    val pp: Int?,

    @Column(name = "accuracy")
    val accuracy: Int?,

    @Column(name = "priority")
    val priority: Int,

    @Column(name = "target_id")
    val targetId: Int?,

    @Column(name = "damage_class_id")
    val damageClassId: Int?,

    @Column(name = "effect_id")
    val effectId: Int?,

    @Column(name = "effect_chance")
    val effectChance: Int?,

    @Column(name = "contest_type_id")
    val contestTypeId: Int?,

    @Column(name = "contest_effect_id")
    val contestEffectId: Int?,

    @Column(name = "super_contest_effect_id")
    val superContestEffectId: Int?
)

@Entity
@Table(name = "pokemon_colors")
data class PokemonColor(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "pokemon_shapes")
data class PokemonShape(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "pokemon_habitats")
data class PokemonHabitat(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "evolution_chains")
data class EvolutionChain(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "baby_trigger_item_id")
    val babyTriggerItemId: Int?
)

@Entity
@Table(name = "pokemon_species")
data class PokemonSpecies(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "generation_id")
    val generationId: Int,

    @Column(name = "evolves_from_species_id")
    val evolvesFromSpeciesId: Int?,

    @Column(name = "evolution_chain_id")
    val evolutionChainId: Int,

    @Column(name = "color_id")
    val colorId: Int,

    @Column(name = "shape_id")
    val shapeId: Int,

    @Column(name = "habitat_id")
    val habitatId: Int,

    @Column(name = "gender_rate")
    val genderRate: Int,

    @Column(name = "capture_rate")
    val captureRate: Int,

    @Column(name = "base_happiness")
    val baseHappiness: Int,

    @Column(name = "is_baby")
    val isBaby: Boolean,

    @Column(name = "hatch_counter")
    val hatchCounter: Int?,

    @Column(name = "has_gender_differences")
    val hasGenderDifferences: Boolean,

    @Column(name = "growth_rate_id")
    val growthRateId: Int?,

    @Column(name = "forms_switchable")
    val formsSwitchable: Boolean,

    @Column(name = "is_legendary")
    val isLegendary: Boolean,

    @Column(name = "is_mythical")
    val isMythical: Boolean,

    @Column(name = "order_index")
    val orderIndex: Int?,

    @Column(name = "conquest_order")
    val conquestOrder: Int?
)

@Entity
@Table(name = "pokemon")
data class Pokemon(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "species_id")
    val speciesId: Int,

    @Column(name = "height")
    val height: Int,

    @Column(name = "weight")
    val weight: Int,

    @Column(name = "base_experience")
    val baseExperience: Int,

    @Column(name = "order_index")
    val orderIndex: Int?,

    @Column(name = "is_default")
    val isDefault: Boolean
)

@Entity
@Table(name = "version_groups")
data class VersionGroup(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "generation_id")
    val generationId: Int,

    @Column(name = "group_order")
    val groupOrder: Int?
)

@Entity
@Table(name = "pokemon_forms")
data class PokemonForm(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "form_identifier")
    val formIdentifier: String?,

    @Column(name = "pokemon_id")
    val pokemonId: Int,

    @Column(name = "introduced_in_version_group_id")
    val introducedInVersionGroupId: Int?,

    @Column(name = "is_default")
    val isDefault: Boolean,

    @Column(name = "is_battle_only")
    val isBattleOnly: Boolean,

    @Column(name = "is_mega")
    val isMega: Boolean,

    @Column(name = "form_order")
    val formOrder: Int?,

    @Column(name = "order_index")
    val orderIndex: Int?
)

@Entity
@Table(name = "abilities")
data class Ability(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "generation_id")
    val generationId: Int,

    @Column(name = "is_main_series")
    val isMainSeries: Boolean?
)

@Entity
@Table(name = "locations")
data class Location(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "region_id")
    val regionId: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "pokemon_location_areas")
data class PokemonLocationArea(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "location_id")
    val locationId: Int,

    @Column(name = "game_index")
    val gameIndex: Int,

    @Column(name = "identifier")
    val identifier: String?
)

@Entity
@Table(name = "contest_types")
data class ContestType(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "contest_effects")
data class ContestEffect(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "appeal")
    val appeal: Int,

    @Column(name = "jam")
    val jam: Int
)

@Entity
@Table(name = "item_categories")
data class ItemCategory(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "pocket_id")
    val pocketId: Int?,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "items")
data class Item(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "category_id")
    val categoryId: Int?,

    @Column(name = "cost")
    val cost: Int,

    @Column(name = "fling_power")
    val flingPower: Int?,

    @Column(name = "fling_effect_id")
    val flingEffectId: Int?
)

@Entity
@Table(name = "pokemon_move_methods")
data class PokemonMoveMethod(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String
)

@Entity
@Table(name = "berries")
data class Berry(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "identifier")
    val identifier: String,

    @Column(name = "growth_time")
    val growthTime: Int?,

    @Column(name = "max_harvest")
    val maxHarvest: Int?,

    @Column(name = "natural_gift_power")
    val naturalGiftPower: Int?,

    @Column(name = "size")
    val size: Int?,

    @Column(name = "smoothness")
    val smoothness: Int?,

    @Column(name = "item_id")
    val itemId: Int?
)

// --- Classes @Embeddable para Chaves Compostas ---

@Embeddable
data class MoveEffectProseId(
    @Column(name = "move_effect_id")
    val moveEffectId: Int,
    @Column(name = "local_language_id")
    val localLanguageId: Int
) : Serializable

@Embeddable
data class PokemonStatId(
    @Column(name = "pokemon_id")
    val pokemonId: Int,
    @Column(name = "stat_id")
    val statId: Int
) : Serializable

@Embeddable
data class PokemonTypeId(
    @Column(name = "pokemon_id")
    val pokemonId: Int,
    @Column(name = "type_id")
    val typeId: Int
) : Serializable

@Embeddable
data class PokemonAbilityId(
    @Column(name = "pokemon_id")
    val pokemonId: Int,
    @Column(name = "ability_id")
    val abilityId: Int
) : Serializable

@Embeddable
data class PokemonEggGroupId(
    @Column(name = "pokemon_species_id")
    val pokemonSpeciesId: Int,
    @Column(name = "egg_group_id")
    val eggGroupId: Int
) : Serializable

@Embeddable
data class AbilityProseId(
    @Column(name = "ability_id")
    val abilityId: Int,
    @Column(name = "local_language_id")
    val localLanguageId: Int
) : Serializable

@Embeddable
data class AbilityFlavorTextId(
    @Column(name = "ability_id")
    val abilityId: Int,
    @Column(name = "version_group_id")
    val versionGroupId: Int,
    @Column(name = "language_id")
    val languageId: Int
) : Serializable

@Embeddable
data class PokemonMoveId(
    @Column(name = "pokemon_id")
    val pokemonId: Int,
    @Column(name = "version_group_id")
    val versionGroupId: Int,
    @Column(name = "move_id")
    val moveId: Int,
    @Column(name = "pokemon_move_method_id")
    val pokemonMoveMethodId: Int
) : Serializable

@Embeddable
data class BerryFlavorId(
    @Column(name = "berry_id")
    val berryId: Int,
    @Column(name = "contest_type_id")
    val contestTypeId: Int
) : Serializable

// --- Entidades com Chave Primária Composta (@EmbeddedId) ---

@Entity
@Table(name = "move_effect_prose")
data class MoveEffectProse(
    @EmbeddedId
    val id: MoveEffectProseId,

    @Column(name = "short_effect")
    val shortEffect: String?,

    @Column(name = "effect")
    val effect: String?
)

@Entity
@Table(name = "pokemon_stats")
data class PokemonStat(
    @EmbeddedId
    val id: PokemonStatId,

    @Column(name = "base_stat")
    val baseStat: Int,

    @Column(name = "effort")
    val effort: Int
)

@Entity
@Table(name = "pokemon_types")
data class PokemonType(
    @EmbeddedId
    val id: PokemonTypeId,

    @Column(name = "slot")
    val slot: Int
)

@Entity
@Table(name = "pokemon_abilities")
data class PokemonAbility(
    @EmbeddedId
    val id: PokemonAbilityId,

    @Column(name = "is_hidden")
    val isHidden: Boolean,

    @Column(name = "slot")
    val slot: Int
)

@Entity
@Table(name = "pokemon_egg_groups")
data class PokemonEggGroup(
    @EmbeddedId
    val id: PokemonEggGroupId
    // Se não houver outras colunas além da PK composta, a entidade pode ser assim.
)

@Entity
@Table(name = "ability_prose")
data class AbilityProse(
    @EmbeddedId
    val id: AbilityProseId,

    @Column(name = "short_effect")
    val shortEffect: String?,

    @Column(name = "effect")
    val effect: String?
)

@Entity
@Table(name = "ability_flavor_text")
data class AbilityFlavorText(
    @EmbeddedId
    val id: AbilityFlavorTextId,

    @Column(name = "flavor_text")
    val flavorText: String
)

@Entity
@Table(name = "pokemon_moves")
data class PokemonMove(
    @EmbeddedId
    val id: PokemonMoveId,

    @Column(name = "level")
    val level: Int,

    @Column(name = "move_order")
    val moveOrder: Int?,

    @Column(name = "mastery")
    val mastery: Int?
)

@Entity
@Table(name = "berry_flavors")
data class BerryFlavor(
    @EmbeddedId
    val id: BerryFlavorId,

    @Column(name = "flavor_id")
    val flavorId: Int
)