package com.pokedex.bff.controllers.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

// DTOs básicos para entidades sem muitos relacionamentos complexos
data class RegionDTO(
    val id: Int,
    val name: String
)

data class TypeDTO(
    val id: Int,
    val name: String,
    val color: String?
)

data class EggGroupDTO(
    val id: Int,
    val name: String
)

data class SpeciesDTO(
    val id: Int,
    val nationalPokedexNumber: String,
    val name: String,
    val species_en: String? = null,
    val species_pt: String? = null
)

data class GenerationDTO(
    val id: Int,
    val name: String,
    val region: RegionDTO?
)

data class AbilityDTO(
    val id: Int,
    val name: String,
    val description: String? = null,
    val introducedGeneration: GenerationDTO? = null
)

data class StatsDTO(
    val id: Int,
    val pokemonNationalPokedexNumber: String,
    val pokemonName: String,
    val total: Int,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int
)

data class PokemonTypeDTO(
    val type: TypeDTO
)

data class PokemonAbilityDTO(
    val ability: AbilityDTO,
    val isHidden: Boolean
)

data class PokemonEggGroupDTO(
    val eggGroup: EggGroupDTO
)

data class WeaknessDTO(
    val weaknessType: String
)

data class EvolutionDetailDTO(
    val id: Int,
    val pokemonId: Int,
    val targetPokemonId: Int?,
    val targetPokemonName: String?,
    val conditionType: String?,
    val conditionValue: String?
)

data class EvolutionChainDTO(
    val id: Int,
    val chain: List<EvolutionDetailDTO>
)

data class PokemonDTO(
    val id: Int,
    val nationalPokedexNumber: String,
    val name: String,
    val stats: StatsDTO?,
    val generation: GenerationDTO?,
    val species: SpeciesDTO?,
    val heightM: Double?,
    val weightKg: Double?,
    val description: String?,
    val sprites: Map<String, Any>?,
    val genderRateValue: Int?,
    val eggCycles: Int?,
    val evolutionChain: EvolutionChainDTO?,
    val types: List<TypeDTO>,
    val abilities: List<PokemonAbilityDTO>,
    val eggGroups: List<EggGroupDTO> // <--- ALTERADO AQUI para List<EggGroupDTO>
)

// DTOs de ENTRADA (Input DTOs)
data class RegionInputDTO(
    val id: Int,
    val name: String
)

data class TypeInputDTO(
    val id: Int,
    val name: String,
    val color: String?
)

data class EggGroupInputDTO(
    val id: Int,
    val name: String
)

data class SpeciesInputDTO(
    val id: Int,
    @JsonProperty("national_pokedex_number") val nationalPokedexNumber: String,
    val name: String,
    @JsonProperty("species_en") val species_en: String?,
    @JsonProperty("species_pt") val species_pt: String?
)

data class GenerationInputDTO(
    val id: Int,
    val name: String,
    @JsonProperty("regiaoId") val regionId: Int?
)

data class AbilityInputDTO(
    val id: Int,
    val name: String,
    val description: String?,
    @JsonProperty("introduced_generation_id") val introducedGenerationId: Int?
)

data class StatsInputDTO(
    @JsonProperty("pokemon_national_pokedex_number") val pokemonNationalPokedexNumber: String,
    @JsonProperty("pokemon_name") val pokemonName: String?,
    val total: Int,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    @JsonProperty("sp_atk") val spAtk: Int,
    @JsonProperty("sp_def") val spDef: Int,
    val speed: Int
)

data class PokemonInputDTO(
    val id: Int,
    @JsonProperty("national_pokedex_number") val nationalPokedexNumber: String,
    val name: String,
    @JsonProperty("generation_id") val generationId: Int,
    @JsonProperty("species_id") val speciesId: Int,
    @JsonProperty("height_m") val heightM: Double?,
    @JsonProperty("weight_kg") val weightKg: Double?,
    val description: String?,
    val sprites: Map<String, Any>?,
    @JsonProperty("gender_rate_value") val genderRateValue: Int?,
    @JsonProperty("egg_cycles") val eggCycles: Int?,
    @JsonProperty("type_ids") val typeIds: List<Int>?,
    val abilities: List<PokemonAbilityInputDTO>?,
    @JsonProperty("egg_group_ids") val eggGroupIds: List<Int>?
)

data class PokemonAbilityInputDTO(
    @JsonProperty("ability_id") val abilityId: Int,
    @JsonProperty("is_hidden") val isHidden: Boolean
)

// Weakness JSON source DTO (WeaknessInputDTO)
data class WeaknessInputDTO(
    val id: Int?,
    @JsonProperty("pokemon_id") val pokemonId: Int?,
    @JsonProperty("pokemon_name") val pokemonName: String?,
    val weaknesses: List<String>
)

// Evolution Chain JSON source DTO (EvolutionChainInputDTO)
data class EvolutionChainInputDTO(
    val id: Int,
    val chain: List<EvolutionLinkInputDTO>
)

data class EvolutionLinkInputDTO(
    @JsonProperty("pokemon_id") val pokemonId: Int,
    @JsonProperty("evolution_details") val evolutionDetails: List<EvolutionDetailInputDTO>
)

data class EvolutionDetailInputDTO(
    @JsonProperty("target_pokemon_id") val targetPokemonId: Int?,
    val condition: EvolutionConditionInputDTO?
)

data class EvolutionConditionInputDTO(
    @JsonProperty("min_level") val minLevel: Int?,
    val trigger: String?,
    val item: String?,
    @JsonProperty("held_item") val heldItem: String?,
    val gender: Int?,
    @JsonProperty("time_of_day") val timeOfDay: String?,
    val location: String?,
    @JsonProperty("party_species") val partySpecies: String?,
    @JsonProperty("party_type") val partyType: String?,
    @JsonProperty("trade_species") val tradeSpecies: String?,
    @JsonProperty("needs_overworld_rain") val needsOverworldRain: Boolean?,
    @JsonProperty("turn_upside_down") val turnUpsideDown: Boolean?,
    @JsonProperty("known_move") val knownMove: String?,
    @JsonProperty("known_move_type") val knownMoveType: String?,
    @JsonProperty("min_happiness") val minHappiness: Int?,
    @JsonProperty("min_beauty") val minBeauty: Int?,
    @JsonProperty("min_affection") val minAffection: Int?,
    @JsonProperty("relative_physical_stats") val relativePhysicalStats: Int?,
    @JsonProperty("pre_trigger_evolution") val preTriggerEvolution: Boolean?,
    val happy: Boolean?,
    val beauty: Boolean?,
    val affection: Boolean?
)


// Funções de extensão para converter Entidades em DTOs de SAÍDA

fun Region.toDTO(): RegionDTO = RegionDTO(id = id, name = name)

fun Type.toDTO(): TypeDTO = TypeDTO(id = id, name = name, color = color)

fun EggGroup.toDTO(): EggGroupDTO = EggGroupDTO(id = id, name = name)

fun Species.toDTO(): SpeciesDTO = SpeciesDTO(
    id = id,
    nationalPokedexNumber = nationalPokedexNumber,
    name = name,
    species_en = species_en,
    species_pt = species_pt
)

fun Generation.toDTO(): GenerationDTO = GenerationDTO(
    id = id,
    name = name,
    region = region?.toDTO()
)

fun Ability.toDTO(): AbilityDTO = AbilityDTO(
    id = id,
    name = name,
    description = description,
    introducedGeneration = introducedGeneration?.toDTO()
)

fun Stats.toDTO(): StatsDTO = StatsDTO(
    id = id,
    pokemonNationalPokedexNumber = pokemonNationalPokedexNumber,
    pokemonName = pokemonName,
    total = total,
    hp = hp,
    attack = attack,
    defense = defense,
    spAtk = spAtk,
    spDef = spDef,
    speed = speed
)

fun EvolutionDetail.toDTO(): EvolutionDetailDTO = EvolutionDetailDTO(
    id = id,
    pokemonId = pokemon.id,
    targetPokemonId = targetPokemon?.id,
    targetPokemonName = targetPokemonName,
    conditionType = condition_type,
    conditionValue = condition_value
)

fun EvolutionChain.toDTO(): EvolutionChainDTO = EvolutionChainDTO(
    id = id,
    chain = evolutionDetails.map { it.toDTO() }.sortedBy { it.targetPokemonId }
)

fun Weakness.toDTO(): WeaknessDTO = WeaknessDTO(
    weaknessType = weakness_type
)

fun PokemonType.toDTO(): PokemonTypeDTO = PokemonTypeDTO(type = this.type.toDTO())

fun PokemonAbility.toDTO(): PokemonAbilityDTO = PokemonAbilityDTO(
    ability = this.ability.toDTO(),
    isHidden = this.isHidden
)

fun PokemonEggGroup.toDTO(): PokemonEggGroupDTO = PokemonEggGroupDTO(eggGroup = this.eggGroup.toDTO())


fun Pokemon.toDTO(): PokemonDTO {
    val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
    }

    val parsedSprites: Map<String, Any>? = this.sprites?.let { spriteString ->
        try {
            objectMapper.readValue<Map<String, Any>>(spriteString)
        } catch (e: Exception) {
            System.err.println("Aviso: Não foi possível parsear o JSON de sprites para o Pokémon ${this.nationalPokedexNumber}: ${e.message}")
            null
        }
    }

    return PokemonDTO(
        id = id,
        nationalPokedexNumber = nationalPokedexNumber,
        name = name,
        stats = stats?.toDTO(),
        generation = generation?.toDTO(),
        species = species?.toDTO(),
        heightM = heightM,
        weightKg = weightKg,
        description = description,
        genderRateValue = gender_rate_value,
        eggCycles = eggCycles,
        evolutionChain = evolutionChain?.toDTO(),
        types = pokemonTypes.map { it.toDTO().type },
        abilities = pokemonAbilities.map { it.toDTO() },
        eggGroups = pokemonEggGroups.map { it.toDTO().eggGroup },
        sprites = parsedSprites
    )
}