package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.AbilityEntity
import com.pokedex.bff.domain.repositories.AbilityRepository
import com.pokedex.bff.domain.repositories.GenerationRepository
import com.pokedex.bff.application.dto.seeder.AbilityDto
import com.pokedex.bff.infrastructure.seeder.data.EntityType
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.exception.DataImportException
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.seeder.util.importData
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(5)
class AbilityImportStrategy(
    private val abilityRepository: AbilityRepository,
    private val generationRepository: GenerationRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(AbilityImportStrategy::class.java)
        private val entityName = EntityType.ABILITY.entityName
    }

    override fun getEntityName(): String = entityName

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $entityName...")
        val generationsMap = generationRepository.findAll().associateBy { it.id }
        val dtos: List<AbilityDto> = jsonLoader.loadJson(JsonFile.ABILITIES.filePath)

        val counts = importData(dtos, abilityRepository, { dto ->
            val generation = generationsMap[dto.introducedGenerationId]
                ?: throw DataImportException("Generation with ID ${dto.introducedGenerationId} not found for Ability ${dto.name}")
            AbilityEntity(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                introducedGeneration = generation
            )
        }, logger, entityName)
        results.add(entityName, counts)
        return counts
    }
}
