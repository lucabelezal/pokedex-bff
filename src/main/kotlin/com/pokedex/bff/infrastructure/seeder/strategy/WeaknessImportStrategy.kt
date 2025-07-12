package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.domain.repositories.TypeRepository
import com.pokedex.bff.application.dto.seeder.WeaknessDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Order(10)
class WeaknessImportStrategy(
    private val pokemonRepository: PokemonRepository,
    private val typeRepository: TypeRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(WeaknessImportStrategy::class.java)
        private const val ENTITY_NAME = "Fraquezas"
    }

    override fun getEntityName(): String = ENTITY_NAME

    @Transactional // Each pokemon's weaknesses update should be transactional
    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val dtos: List<WeaknessDto> = jsonLoader.loadJson(JsonFile.WEAKNESSES.filePath)
        val counts = ImportCounts()

        // Preload data
        val pokemonMap = pokemonRepository.findAll().associateBy { it.name }
        val typeMap = typeRepository.findAll().associateBy { it.name }

        dtos.forEach { dto ->
            try {
                val pokemon = pokemonMap[dto.pokemonName]
                if (pokemon == null) {
                    logger.warn("Pokémon '${dto.pokemonName}' não encontrado para importar fraquezas")
                    counts.errors++
                    return@forEach // continue to next dto
                }

                val weaknessTypesFound = dto.weaknesses.mapNotNull { typeName ->
                    typeMap[typeName]?.also { typeEntity ->
                        // Add if not already present (PokemonEntity.weaknesses should be a Set)
                        if (pokemon.weaknesses.add(typeEntity)) {
                            // if add returns true, it means it was added
                        }
                    } ?: run {
                        logger.warn("Tipo '$typeName' não encontrado para fraqueza do Pokémon ${dto.pokemonName}")
                        null // type not found, will be filtered out by mapNotNull
                    }
                }

                if (weaknessTypesFound.isNotEmpty()) {
                     // Check if any actual new weaknesses were added.
                     // The pokemon.weaknesses.add inside mapNotNull already modified the set.
                     // We just need to save if there were valid types to add.
                    pokemonRepository.save(pokemon)
                    counts.success++
                } else if (dto.weaknesses.any { typeMap[it] == null }) {
                    // If there were type names in DTO but none were found in DB, count as error.
                    counts.errors++
                }
                // If dto.weaknesses was empty, or all types were already present, it's not an error nor a direct success for this DTO.
                // The current logic counts a success if any valid weakness type is processed and saved.

            } catch (e: Exception) {
                counts.errors++
                logger.error("Erro importando fraquezas para ${dto.pokemonName}: ${e.message}", e)
            }
        }

        results.add(ENTITY_NAME, counts)
        return counts
    }
}
