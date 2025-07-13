package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.RegionEntity
import com.pokedex.bff.domain.repositories.RegionRepository
import com.pokedex.bff.application.dto.seeder.RegionDto
import com.pokedex.bff.infrastructure.seeder.data.EntityType
import com.pokedex.bff.infrastructure.seeder.dto.ImportCounts
import com.pokedex.bff.infrastructure.seeder.dto.ImportResults
import com.pokedex.bff.infrastructure.seeder.util.JsonLoader
import com.pokedex.bff.infrastructure.seeder.util.importData
import com.pokedex.bff.infrastructure.utils.JsonFile
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(1)
class RegionImportStrategy(
    private val regionRepository: RegionRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(RegionImportStrategy::class.java)
        private val entityName = EntityType.REGION.entityName
    }

    override fun getEntityName(): String = entityName

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $entityName...")
        val dtos: List<RegionDto> = jsonLoader.loadJson(JsonFile.REGIONS.filePath)
        val counts = importData(dtos, regionRepository, { dto ->
            RegionEntity(id = dto.id, name = dto.name)
        }, logger, entityName)
        results.add(entityName, counts)
        return counts
    }
}
