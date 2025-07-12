package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.GenerationEntity
import com.pokedex.bff.domain.repositories.GenerationRepository
import com.pokedex.bff.domain.repositories.RegionRepository
import com.pokedex.bff.application.dto.seeder.GenerationDto
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
@Order(4)
class GenerationImportStrategy(
    private val generationRepository: GenerationRepository,
    private val regionRepository: RegionRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(GenerationImportStrategy::class.java)
        private val entityName = EntityType.GENERATION.entityName
    }

    override fun getEntityName(): String = entityName

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $entityName...")
        val regionsMap = regionRepository.findAll().associateBy { it.id }
        val dtos: List<GenerationDto> = jsonLoader.loadJson(JsonFile.GENERATIONS.filePath)

        val counts = importData(dtos, generationRepository, { dto ->
            val region = regionsMap[dto.regionId]
                ?: throw DataImportException("Region with ID ${dto.regionId} not found for Generation ${dto.name}")
            GenerationEntity(id = dto.id, name = dto.name, region = region)
        }, logger, entityName)
        results.add(entityName, counts)
        return counts
    }
}
