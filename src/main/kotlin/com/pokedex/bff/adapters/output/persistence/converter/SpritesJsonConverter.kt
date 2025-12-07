package com.pokedex.bff.adapters.output.persistence.converter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.pokedex.bff.domain.pokemon.entities.*
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.slf4j.LoggerFactory

@Converter
class SpritesJsonConverter : AttributeConverter<Sprites?, String?> {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val logger = LoggerFactory.getLogger(SpritesJsonConverter::class.java)

    override fun convertToDatabaseColumn(sprites: Sprites?): String? {
        return sprites?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(json: String?): Sprites? {
        return json?.takeIf { it.isNotBlank() }?.let {
            try {
                val dto = objectMapper.readValue<SpritesDTO>(it)
                dto.toDomain()
            } catch (e: Exception) {
                logger.error("Error deserializing sprites from JSON: ${e.message}", e)
                null
            }
        }
    }

    // DTOs com mapeamento correto de snake_case para camelCase
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class SpritesDTO(
        @JsonProperty("back_default") val backDefault: String? = null,
        @JsonProperty("back_female") val backFemale: String? = null,
        @JsonProperty("back_shiny") val backShiny: String? = null,
        @JsonProperty("back_shiny_female") val backShinyFemale: String? = null,
        @JsonProperty("front_default") val frontDefault: String? = null,
        @JsonProperty("front_female") val frontFemale: String? = null,
        @JsonProperty("front_shiny") val frontShiny: String? = null,
        @JsonProperty("front_shiny_female") val frontShinyFemale: String? = null,
        val other: OtherSpritesDTO? = null,
        val versions: GameVersionsSpritesDTO? = null
    ) {
        fun toDomain() = Sprites(
            backDefault = backDefault,
            backFemale = backFemale,
            backShiny = backShiny,
            backShinyFemale = backShinyFemale,
            frontDefault = frontDefault,
            frontFemale = frontFemale,
            frontShiny = frontShiny,
            frontShinyFemale = frontShinyFemale,
            other = other?.toDomain(),
            versions = versions?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class OtherSpritesDTO(
        val home: HomeSpritesDTO? = null,
        val showdown: ShowdownSpritesDTO? = null,
        @JsonProperty("dream_world") val dreamWorld: DreamWorldSpritesDTO? = null,
        @JsonProperty("official-artwork") val officialArtwork: OfficialArtworkSpritesDTO? = null
    ) {
        fun toDomain() = OtherSprites(
            home = home?.toDomain(),
            showdown = showdown?.toDomain(),
            dreamWorld = dreamWorld?.toDomain(),
            officialArtwork = officialArtwork?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class HomeSpritesDTO(
        @JsonProperty("front_default") val frontDefault: String? = null,
        @JsonProperty("front_shiny") val frontShiny: String? = null,
        @JsonProperty("front_female") val frontFemale: String? = null,
        @JsonProperty("front_shiny_female") val frontShinyFemale: String? = null
    ) {
        fun toDomain() = HomeSprites(
            frontDefault = frontDefault,
            frontShiny = frontShiny,
            frontFemale = frontFemale,
            frontShinyFemale = frontShinyFemale
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ShowdownSpritesDTO(
        @JsonProperty("front_default") val frontDefault: String? = null,
        @JsonProperty("front_shiny") val frontShiny: String? = null,
        @JsonProperty("front_female") val frontFemale: String? = null,
        @JsonProperty("front_shiny_female") val frontShinyFemale: String? = null,
        @JsonProperty("back_default") val backDefault: String? = null,
        @JsonProperty("back_shiny") val backShiny: String? = null,
        @JsonProperty("back_female") val backFemale: String? = null,
        @JsonProperty("back_shiny_female") val backShinyFemale: String? = null
    ) {
        fun toDomain() = ShowdownSprites(
            frontDefault = frontDefault,
            frontShiny = frontShiny,
            frontFemale = frontFemale,
            frontShinyFemale = frontShinyFemale,
            backDefault = backDefault,
            backShiny = backShiny,
            backFemale = backFemale,
            backShinyFemale = backShinyFemale
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class DreamWorldSpritesDTO(
        @JsonProperty("front_default") val frontDefault: String? = null,
        @JsonProperty("front_female") val frontFemale: String? = null
    ) {
        fun toDomain() = DreamWorldSprites(
            frontDefault = frontDefault,
            frontFemale = frontFemale
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class OfficialArtworkSpritesDTO(
        @JsonProperty("front_default") val frontDefault: String? = null,
        @JsonProperty("front_shiny") val frontShiny: String? = null
    ) {
        fun toDomain() = OfficialArtworkSprites(
            frontDefault = frontDefault,
            frontShiny = frontShiny
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GameVersionsSpritesDTO(
        @JsonProperty("generation-i") val generationI: GenerationISpritesDTO? = null,
        @JsonProperty("generation-ii") val generationII: GenerationIISpritesDTO? = null,
        @JsonProperty("generation-iii") val generationIII: GenerationIIISpritesDTO? = null,
        @JsonProperty("generation-iv") val generationIV: GenerationIVSpritesDTO? = null,
        @JsonProperty("generation-v") val generationV: GenerationVSpritesDTO? = null,
        @JsonProperty("generation-vi") val generationVI: GenerationVISpritesDTO? = null,
        @JsonProperty("generation-vii") val generationVII: GenerationVIISpritesDTO? = null,
        @JsonProperty("generation-viii") val generationVIII: GenerationVIIISpritesDTO? = null
    ) {
        fun toDomain() = GameVersionsSprites(
            generationI = generationI?.toDomain(),
            generationII = generationII?.toDomain(),
            generationIII = generationIII?.toDomain(),
            generationIV = generationIV?.toDomain(),
            generationV = generationV?.toDomain(),
            generationVI = generationVI?.toDomain(),
            generationVII = generationVII?.toDomain(),
            generationVIII = generationVIII?.toDomain()
        )
    }

    // Simplificando as gerações (mapeie de acordo com sua necessidade)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationISpritesDTO(
        @JsonProperty("red-blue") val redBlue: GameSpritesDTO? = null,
        val yellow: GameSpritesDTO? = null
    ) {
        fun toDomain() = GenerationISprites(
            redBlue = redBlue?.toDomain(),
            yellow = yellow?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationIISpritesDTO(
        val crystal: GameSpritesDTO? = null,
        val gold: GameSpritesDTO? = null,
        val silver: GameSpritesDTO? = null
    ) {
        fun toDomain() = GenerationIISprites(
            crystal = crystal?.toDomain(),
            gold = gold?.toDomain(),
            silver = silver?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationIIISpritesDTO(
        val emerald: GameSpritesDTO? = null,
        @JsonProperty("firered-leafgreen") val fireredLeafgreen: GameSpritesDTO? = null,
        @JsonProperty("ruby-sapphire") val rubySapphire: GameSpritesDTO? = null
    ) {
        fun toDomain() = GenerationIIISprites(
            emerald = emerald?.toDomain(),
            fireredLeafgreen = fireredLeafgreen?.toDomain(),
            rubySapphire = rubySapphire?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationIVSpritesDTO(
        @JsonProperty("diamond-pearl") val diamondPearl: GameSpritesDTO? = null,
        @JsonProperty("heartgold-soulsilver") val heartgoldSoulsilver: GameSpritesDTO? = null,
        val platinum: GameSpritesDTO? = null
    ) {
        fun toDomain() = GenerationIVSprites(
            diamondPearl = diamondPearl?.toDomain(),
            heartgoldSoulsilver = heartgoldSoulsilver?.toDomain(),
            platinum = platinum?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationVSpritesDTO(
        @JsonProperty("black-white") val blackWhite: AnimatedSpritesDTO? = null
    ) {
        fun toDomain() = GenerationVSprites(
            blackWhite = blackWhite?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationVISpritesDTO(
        @JsonProperty("omegaruby-alphasapphire") val omegarubyAlphasapphire: GameSpritesDTO? = null,
        @JsonProperty("x-y") val xY: GameSpritesDTO? = null
    ) {
        fun toDomain() = GenerationVISprites(
            omegarubyAlphasapphire = omegarubyAlphasapphire?.toDomain(),
            xY = xY?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationVIISpritesDTO(
        val icons: GameSpritesDTO? = null,
        @JsonProperty("ultra-sun-ultra-moon") val ultraSunUltraMoon: GameSpritesDTO? = null
    ) {
        fun toDomain() = GenerationVIISprites(
            icons = icons?.toDomain(),
            ultraSunUltraMoon = ultraSunUltraMoon?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GenerationVIIISpritesDTO(
        val icons: GameSpritesDTO? = null
    ) {
        fun toDomain() = GenerationVIIISprites(
            icons = icons?.toDomain()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GameSpritesDTO(
        @JsonProperty("back_default") val backDefault: String? = null,
        @JsonProperty("back_female") val backFemale: String? = null,
        @JsonProperty("back_shiny") val backShiny: String? = null,
        @JsonProperty("back_shiny_female") val backShinyFemale: String? = null,
        @JsonProperty("front_default") val frontDefault: String? = null,
        @JsonProperty("front_female") val frontFemale: String? = null,
        @JsonProperty("front_shiny") val frontShiny: String? = null,
        @JsonProperty("front_shiny_female") val frontShinyFemale: String? = null,
        @JsonProperty("back_gray") val backGray: String? = null,
        @JsonProperty("back_transparent") val backTransparent: String? = null,
        @JsonProperty("front_gray") val frontGray: String? = null,
        @JsonProperty("front_transparent") val frontTransparent: String? = null
    ) {
        fun toDomain() = GameSprites(
            backDefault = backDefault,
            backFemale = backFemale,
            backShiny = backShiny,
            backShinyFemale = backShinyFemale,
            frontDefault = frontDefault,
            frontFemale = frontFemale,
            frontShiny = frontShiny,
            frontShinyFemale = frontShinyFemale,
            backGray = backGray,
            backTransparent = backTransparent,
            frontGray = frontGray,
            frontTransparent = frontTransparent
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class AnimatedSpritesDTO(
        val animated: GameSpritesDTO? = null,
        @JsonProperty("back_default") val backDefault: String? = null,
        @JsonProperty("back_female") val backFemale: String? = null,
        @JsonProperty("back_shiny") val backShiny: String? = null,
        @JsonProperty("back_shiny_female") val backShinyFemale: String? = null,
        @JsonProperty("front_default") val frontDefault: String? = null,
        @JsonProperty("front_female") val frontFemale: String? = null,
        @JsonProperty("front_shiny") val frontShiny: String? = null,
        @JsonProperty("front_shiny_female") val frontShinyFemale: String? = null
    ) {
        fun toDomain() = AnimatedSprites(
            animated = animated?.toDomain(),
            backDefault = backDefault,
            backFemale = backFemale,
            backShiny = backShiny,
            backShinyFemale = backShinyFemale,
            frontDefault = frontDefault,
            frontFemale = frontFemale,
            frontShiny = frontShiny,
            frontShinyFemale = frontShinyFemale
        )
    }
}
