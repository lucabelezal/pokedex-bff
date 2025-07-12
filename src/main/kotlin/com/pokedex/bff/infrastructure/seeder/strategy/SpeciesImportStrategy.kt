package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.SpeciesEntity
import com.pokedex.bff.domain.repositories.SpeciesRepository
import com.pokedex.bff.application.dto.seeder.SpeciesDto
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
@Order(6)
class SpeciesImportStrategy(
    private val speciesRepository: SpeciesRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(SpeciesImportStrategy::class.java)
        private val entityName = EntityType.SPECIES.entityName
    }

    override fun getEntityName(): String = entityName

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $entityName...")
        val dtos: List<SpeciesDto> = jsonLoader.loadJson(JsonFile.SPECIES.filePath)
        val counts = importData(dtos, speciesRepository, { dto ->
            SpeciesEntity(id = dto.id, name = dto.name)
        }, logger, entityName)
        results.add(entityName, counts)
        return counts
    }
}
