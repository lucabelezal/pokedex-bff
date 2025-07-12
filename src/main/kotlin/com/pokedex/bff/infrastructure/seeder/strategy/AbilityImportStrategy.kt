package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.AbilityEntity
import com.pokedex.bff.domain.repositories.AbilityRepository
import com.pokedex.bff.domain.repositories.GenerationRepository
import com.pokedex.bff.application.dto.seeder.AbilityDto
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class AbilityImportStrategy(
    private val abilityRepository: AbilityRepository,
    private val generationRepository: GenerationRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(AbilityImportStrategy::class.java)
        private const val ENTITY_NAME = "Habilidades"
    }

    override fun getEntityName(): String = ENTITY_NAME

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $ENTITY_NAME...")
        val generationsMap = generationRepository.findAll().associateBy { it.id }
        val dtos: List<AbilityDto> = jsonLoader.loadJson(JsonFile.ABILITIES.filePath)

        val counts = importDependentData(dtos, abilityRepository) { dto ->
            val generation = generationsMap[dto.introducedGenerationId]
                ?: throw DataImportException("Generation with ID ${dto.introducedGenerationId} not found for Ability ${dto.name}")
            AbilityEntity(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                introducedGeneration = generation
            )
        }
        results.add(ENTITY_NAME, counts)
        return counts
    }

    private fun <D, T : Any> importDependentData(
        dtos: List<D>,
        repository: JpaRepository<T, Long>,
        transform: (D) -> T
    ): ImportCounts {
        val counts = ImportCounts()
        dtos.forEach { dto ->
            try {
                repository.save(transform(dto))
                counts.success++
            } catch (e: DataImportException) {
                counts.errors++
                logger.error("Data dependency error for $ENTITY_NAME with value $dto: ${e.message}")
            } catch (e: Exception) {
                counts.errors++
                logger.error("Error importing data for $ENTITY_NAME with value $dto: ${e.message}", e)
            }
        }
        return counts
    }
}
