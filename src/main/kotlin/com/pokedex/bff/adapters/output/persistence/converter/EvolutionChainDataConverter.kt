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
class EvolutionChainDataConverter : AttributeConverter<EvolutionChainData?, String?> {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val logger = LoggerFactory.getLogger(EvolutionChainDataConverter::class.java)

    override fun convertToDatabaseColumn(chainData: EvolutionChainData?): String? {
        return chainData?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(json: String?): EvolutionChainData? {
        return json?.takeIf { it.isNotBlank() }?.let {
            try {
                val dto = objectMapper.readValue<EvolutionChainDataDTO>(it)
                dto.toDomain()
            } catch (e: Exception) {
                logger.error("Error deserializing evolution chain data from JSON: ${e.message}", e)
                null
            }
        }
    }

    // DTOs com mapeamento correto de snake_case para camelCase
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class EvolutionChainDataDTO(
        val pokemon: PokemonEvolutionDTO,
        @JsonProperty("evolutions_to") val evolutionsTo: List<EvolutionStepDTO> = emptyList()
    ) {
        fun toDomain() = EvolutionChainData(
            pokemon = pokemon.toDomain(),
            evolutionsTo = evolutionsTo.map { it.toDomain() }
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class EvolutionStepDTO(
        val pokemon: PokemonEvolutionDTO,
        val condition: EvolutionConditionDTO,
        @JsonProperty("evolutions_to") val evolutionsTo: List<EvolutionStepDTO> = emptyList()
    ) {
        fun toDomain(): EvolutionStep = EvolutionStep(
            pokemon = pokemon.toDomain(),
            condition = condition.toDomain(),
            evolutionsTo = evolutionsTo.map { step -> step.toDomain() }
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class PokemonEvolutionDTO(
        val id: Long,
        val name: String
    ) {
        fun toDomain() = PokemonEvolution(
            id = id,
            name = name
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class EvolutionConditionDTO(
        val type: String,
        val value: Any?,
        val description: String
    ) {
        fun toDomain() = EvolutionCondition(
            type = type,
            value = value,
            description = description
        )
    }
}
