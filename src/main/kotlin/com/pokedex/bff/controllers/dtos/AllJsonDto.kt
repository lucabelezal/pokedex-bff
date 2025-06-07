package com.pokedex.bff.controllers.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.math.BigDecimal

// ========================================================================================================
// DTOs de SAÍDA (Output DTOs)
// Classes usadas para representar os dados que sua API REST irá retornar.
// Geralmente mapeiam diretamente das suas entidades para uma estrutura de resposta.
// ========================================================================================================

data class RegionDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String
)

data class TypeDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("color")
    val color: String?
)

data class EggGroupDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String
)

data class SpeciesDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("national_pokedex_number")
    val nationalPokedexNumber: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("species_en")
    val speciesEn: String? = null,
    @JsonProperty("species_pt")
    val speciesPT: String? = null
)

data class GenerationDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("region")
    val region: RegionDTO?
)

data class AbilityDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("introduced_generation")
    val introducedGeneration: GenerationDTO? = null
)

data class StatsDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("pokemon_national_pokedex_number")
    val pokemonNationalPokedexNumber: String,
    @JsonProperty("pokemon_name")
    val pokemonName: String,
    @JsonProperty("total")
    val total: Int,
    @JsonProperty("hp")
    val hp: Int,
    @JsonProperty("attack")
    val attack: Int,
    @JsonProperty("defense")
    val defense: Int,
    @JsonProperty("sp_atk")
    val spAtk: Int,
    @JsonProperty("sp_def")
    val spDef: Int,
    @JsonProperty("speed")
    val speed: Int
)

data class WeaknessDTO(
    @JsonProperty("weakness_type")
    val weaknessType: String
)

data class EvolutionDetailDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("pokemon_id")
    val pokemonId: Int,
    @JsonProperty("target_pokemon_id")
    val targetPokemonId: Int?,
    @JsonProperty("target_pokemon_name")
    val targetPokemonName: String?,
    @JsonProperty("condition_type")
    val conditionType: String?,
    @JsonProperty("condition_value")
    val conditionValue: String?
)

data class EvolutionChainDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("chain")
    val chain: List<EvolutionDetailDTO>
)

data class PokemonTypeDTO(
    @JsonProperty("type")
    val type: TypeDTO
)

data class PokemonAbilityDTO(
    @JsonProperty("ability")
    val ability: AbilityDTO,
    @JsonProperty("is_hidden")
    val isHidden: Boolean
)

data class PokemonEggGroupDTO(
    @JsonProperty("egg_group")
    val eggGroup: EggGroupDTO
)

data class PokemonDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("national_pokedex_number")
    val nationalPokedexNumber: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("stats")
    val stats: StatsDTO?,
    @JsonProperty("generation")
    val generation: GenerationDTO?,
    @JsonProperty("species")
    val species: SpeciesDTO?,
    @JsonProperty("height_m")
    val height: Double?,
    @JsonProperty("weight_kg")
    val weight: Double?,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("sprites")
    val sprites: Map<String, Any>?,
    @JsonProperty("gender_rate_value")
    val genderRateValue: Int?,
    @JsonProperty("egg_cycles")
    val eggCycles: Int?,
    @JsonProperty("evolution_chain")
    val evolutionChain: EvolutionChainDTO?,
    @JsonProperty("types")
    val types: List<TypeDTO>,
    @JsonProperty("abilities")
    val abilities: List<PokemonAbilityDTO>,
    @JsonProperty("egg_groups")
    val eggGroups: List<EggGroupDTO>
)


// ========================================================================================================
// DTOs de ENTRADA (Input DTOs)
// Classes usadas para representar os dados que sua API REST irá receber.
// Geralmente mapeiam diretamente a estrutura dos seus arquivos JSON de entrada.
// ========================================================================================================

data class RegionInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String
)

data class TypeInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("color")
    val color: String?
)

data class EggGroupInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String
)

data class SpeciesInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("national_pokedex_number")
    val nationalPokedexNumber: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("species_en")
    val speciesEn: String?,
    @JsonProperty("species_pt")
    val speciesPT: String?
)

data class GenerationInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("regiaoId")
    val regionId: Int?
)

data class AbilityInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("introducedGenerationId")
    val introducedGenerationId: Int?
)

data class StatsInputDTO(
    @JsonProperty("pokemon_national_pokedex_number")
    val pokemonNationalPokedexNumber: String,
    @JsonProperty("pokemon_name")
    val pokemonName: String?,
    @JsonProperty("total")
    val total: Int,
    @JsonProperty("hp")
    val hp: Int,
    @JsonProperty("attack")
    val attack: Int,
    @JsonProperty("defense")
    val defense: Int,
    @JsonProperty("sp_atk")
    val spAtk: Int,
    @JsonProperty("sp_def")
    val spDef: Int,
    @JsonProperty("speed")
    val speed: Int
)

data class PokemonAbilityInputDTO(
    @JsonProperty("ability_id")
    val abilityId: Int,
    @JsonProperty("is_hidden")
    val isHidden: Boolean
)

data class WeaknessInputDTO(
    @JsonProperty("id")
    val id: Int?,
    @JsonProperty("pokemon_id")
    val pokemonId: Int?,
    @JsonProperty("pokemon_name")
    val pokemonName: String?,
    @JsonProperty("weaknesses")
    val weaknesses: List<String>
)

data class EvolutionConditionInputDTO(
    @JsonProperty("min_level")
    val minLevel: Int?,
    @JsonProperty("trigger")
    val trigger: String?,
    @JsonProperty("item")
    val item: String?,
    @JsonProperty("held_item")
    val heldItem: String?,
    @JsonProperty("gender")
    val gender: Int?,
    @JsonProperty("time_of_day")
    val timeOfDay: String?,
    @JsonProperty("location")
    val location: String?,
    @JsonProperty("party_species")
    val partySpecies: String?,
    @JsonProperty("party_type")
    val partyType: String?,
    @JsonProperty("trade_species")
    val tradeSpecies: String?,
    @JsonProperty("needs_overworld_rain")
    val needsOverworldRain: Boolean?,
    @JsonProperty("turn_upside_down")
    val turnUpsideDown: Boolean?,
    @JsonProperty("known_move")
    val knownMove: String?,
    @JsonProperty("known_move_type")
    val knownMoveType: String?,
    @JsonProperty("min_happiness")
    val minHappiness: Int?,
    @JsonProperty("min_beauty")
    val minBeauty: Int?,
    @JsonProperty("min_affection")
    val minAffection: Int?,
    @JsonProperty("relative_physical_stats")
    val relativePhysicalStats: Int?,
    @JsonProperty("pre_trigger_evolution")
    val preTriggerEvolution: Boolean?,
    @JsonProperty("happy")
    val happy: Boolean?,
    @JsonProperty("beauty")
    val beauty: Boolean?,
    @JsonProperty("affection")
    val affection: Boolean?
)

data class EvolutionDetailInputDTO(
    @JsonProperty("target_pokemon_id")
    val targetPokemonId: Int?,
    @JsonProperty("condition")
    val condition: EvolutionConditionInputDTO?
)

data class EvolutionLinkInputDTO(
    @JsonProperty("pokemon_id")
    val pokemonId: Int,
    @JsonProperty("evolution_details")
    val evolutionDetails: List<EvolutionDetailInputDTO>?
)

data class EvolutionChainInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("chain")
    val chain: List<EvolutionLinkInputDTO>
)

data class PokemonInputDTO(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("national_pokedex_number")
    val nationalPokedexNumber: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("stats_id")
    val statsId: Int?,
    @JsonProperty("generation_id")
    val generationId: Int,
    @JsonProperty("species_id")
    val speciesId: Int,
    @JsonProperty("height")
    val height: BigDecimal?,
    @JsonProperty("weight")
    val weight: BigDecimal?,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("sprites")
    val sprites: Map<String, Any>?,
    @JsonProperty("gender_rate_value")
    val genderRateValue: Int?,
    @JsonProperty("egg_cycles")
    val eggCycles: Int?,
    @JsonProperty("type_ids")
    val typeIds: List<Int>?,
    @JsonProperty("abilities")
    val abilities: List<PokemonAbilityInputDTO>?,
    @JsonProperty("egg_group_ids")
    val eggGroupIds: List<Int>?
)

// ========================================================================================================
// Funções de EXTENSÃO (Mapeamento de Entidades para DTOs de Saída)
// Estas funções facilitam a conversão das suas classes de entidade para os DTOs de resposta da API.
// ========================================================================================================

fun Region.toDTO(): RegionDTO = RegionDTO(id = id, name = name)

fun Type.toDTO(): TypeDTO = TypeDTO(id = id, name = name, color = color)

fun EggGroup.toDTO(): EggGroupDTO = EggGroupDTO(id = id, name = name)

fun Species.toDTO(): SpeciesDTO = SpeciesDTO(
    id = id,
    nationalPokedexNumber = nationalPokedexNumber,
    name = name,
    speciesEn = speciesEn,
    speciesPT = speciesPT
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
    chain = evolutionDetails.map { it.toDTO() }.sortedBy { it.pokemonId }
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
        generation = generation.toDTO(),
        species = species.toDTO(),
        height = height,
        weight = weight,
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