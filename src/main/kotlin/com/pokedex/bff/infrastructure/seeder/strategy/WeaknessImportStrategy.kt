package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.repositories.PokemonRepository
import com.pokedex.bff.domain.repositories.TypeRepository
import com.pokedex.bff.application.dto.seeder.WeaknessDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
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

    @Transactional
    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val dtos: List<WeaknessDto> = jsonLoader.loadJson(JsonFile.WEAKNESSES.filePath)
        val counts = ImportCounts()

        val pokemonMap = pokemonRepository.findAll().associateBy { it.name }
        val typeMap = typeRepository.findAll().associateBy { it.name }

        dtos.forEach { dto ->
            try {
                val pokemon = pokemonMap[dto.pokemonName]
                if (pokemon == null) {
                    logger.warn("Pokémon '${dto.pokemonName}' não encontrado para importar fraquezas")
                    counts.errors++
                    return@forEach
                }

                val weaknessTypesFound = dto.weaknesses.mapNotNull { typeName ->
                    typeMap[typeName]?.also { typeEntity ->
                        // Add if not already present (PokemonEntity.weaknesses should be a Set)
                        if (pokemon.weaknesses.add(typeEntity)) {
                            // if add returns true, it means it was added
                        }
                    } ?: run {
                        logger.warn("Tipo '$typeName' não encontrado para fraqueza do Pokémon ${dto.pokemonName}")
                        null
                    }
                }

                if (weaknessTypesFound.isNotEmpty()) {
                    pokemonRepository.save(pokemon)
                    counts.success++
                } else if (dto.weaknesses.any { typeMap[it] == null }) {
                    counts.errors++
                }

            } catch (e: Exception) {
                counts.errors++
                logger.error("Erro importando fraquezas para ${dto.pokemonName}: ${e.message}", e)
            }
        }

        results.add(ENTITY_NAME, counts)
        return counts
    }
}
