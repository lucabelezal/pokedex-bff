package com.pokedex.bff.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.pokedex.bff.infra.entity.DreamWorldSprites
import com.pokedex.bff.infra.entity.HomeSprites
import com.pokedex.bff.infra.entity.OfficialArtworkSprites
import com.pokedex.bff.infra.entity.OtherSprites
import com.pokedex.bff.infra.entity.ShowdownSprites
import com.pokedex.bff.infra.entity.Sprites

data class RegionDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String
)

data class TypeDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("color")
    val color: String?
)

data class EggGroupDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String
)

data class SpeciesDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("pokemon_number")
    val pokemonNumber: String?,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("species_en")
    val speciesEn: String?,
    @JsonProperty("species_pt")
    val speciesPt: String?
)

data class GenerationDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("region_id")
    val regionId: Long
)

data class AbilityDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("introduced_generation_id")
    val introducedGenerationId: Long
)

data class StatsDto(
    @JsonProperty("id")
    val id: Long,
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

data class EvolutionChainDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("chain")
    val chainData: Any
)

data class PokemonAbilityDto(
    @JsonProperty("ability_id")
    val abilityId: Long,
    @JsonProperty("is_hidden")
    val isHidden: Boolean
)

data class PokemonDto(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("number")
    val number: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("height")
    val height: Double,
    @JsonProperty("weight")
    val weight: Double,
    @JsonProperty("stats_id")
    val statsId: Long,
    @JsonProperty("generation_id")
    val generationId: Long,
    @JsonProperty("species_id")
    val speciesId: Long,
    @JsonProperty("region_id")
    val regionId: Long?,
    @JsonProperty("evolution_chain_id")
    val evolutionChainId: Long,
    @JsonProperty("gender_rate_value")
    val genderRateValue: Int,
    @JsonProperty("egg_cycles")
    val eggCycles: Int?,
    @JsonProperty("egg_group_ids")
    val eggGroupIds: List<Long>,
    @JsonProperty("type_ids")
    val typeIds: List<Long>,
    @JsonProperty("abilities")
    val abilities: List<PokemonAbilityDto>,
    @JsonProperty("sprites")
    val sprites: SpritesDto
)

data class SpritesDto(
    @JsonProperty("back_default")
    val backDefault: String?,
    @JsonProperty("back_female")
    val backFemale: String?,
    @JsonProperty("back_shiny")
    val backShiny: String?,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String?,
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?,
    @JsonProperty("other")
    val other: OtherSpritesDto?
) {
    fun toModel(): Sprites {
        return Sprites(
            backDefault = this.backDefault,
            backFemale = this.backFemale,
            backShiny = this.backShiny,
            backShinyFemale = this.backShinyFemale,
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale,
            other = this.other?.toModel()
        )
    }
}

data class OtherSpritesDto(
    @JsonProperty("dream_world")
    val dreamWorld: DreamWorldDto?,
    @JsonProperty("home")
    val home: HomeDto?,
    @JsonProperty("official-artwork")
    val officialArtwork: OfficialArtworkDto?,
    @JsonProperty("showdown")
    val showdown: ShowdownDto?
) {
    fun toModel(): OtherSprites {
        return OtherSprites(
            dreamWorld = this.dreamWorld?.toModel(),
            home = this.home?.toModel(),
            officialArtwork = this.officialArtwork?.toModel(),
            showdown = this.showdown?.toModel()
        )
    }
}

data class DreamWorldDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?
) {
    fun toModel(): DreamWorldSprites {
        return DreamWorldSprites(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale
        )
    }
}

data class HomeDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?
) {
    fun toModel(): HomeSprites {
        return HomeSprites(
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale
        )
    }
}

data class OfficialArtworkDto(
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?
) {
    fun toModel(): OfficialArtworkSprites {
        return OfficialArtworkSprites(
            frontDefault = this.frontDefault,
            frontShiny = this.frontShiny
        )
    }
}

data class ShowdownDto(
    @JsonProperty("back_default")
    val backDefault: String?,
    @JsonProperty("back_female")
    val backFemale: String?,
    @JsonProperty("back_shiny")
    val backShiny: String?,
    @JsonProperty("back_shiny_female")
    val backShinyFemale: String?,
    @JsonProperty("front_default")
    val frontDefault: String?,
    @JsonProperty("front_female")
    val frontFemale: String?,
    @JsonProperty("front_shiny")
    val frontShiny: String?,
    @JsonProperty("front_shiny_female")
    val frontShinyFemale: String?
) {
    fun toModel(): ShowdownSprites {
        return ShowdownSprites(
            backDefault = this.backDefault,
            backFemale = this.backFemale,
            backShiny = this.backShiny,
            backShinyFemale = this.backShinyFemale,
            frontDefault = this.frontDefault,
            frontFemale = this.frontFemale,
            frontShiny = this.frontShiny,
            frontShinyFemale = this.frontShinyFemale
        )
    }
}

data class WeaknessDto(
    @JsonProperty("id")
    val id: Long?,
    @JsonProperty("pokemon_id")
    val pokemonId: Long?,
    @JsonProperty("pokemon_name")
    val pokemonName: String,
    @JsonProperty("weaknesses")
    val weaknesses: List<String>
)