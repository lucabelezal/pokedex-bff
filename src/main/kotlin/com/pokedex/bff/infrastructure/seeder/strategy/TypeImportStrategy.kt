package com.pokedex.bff.infrastructure.seeder.strategy

import com.pokedex.bff.domain.entities.TypeEntity
import com.pokedex.bff.domain.repositories.TypeRepository
import com.pokedex.bff.application.dto.seeder.TypeDto
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
@Order(2)
class TypeImportStrategy(
    private val typeRepository: TypeRepository,
    private val jsonLoader: JsonLoader
) : ImportStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(TypeImportStrategy::class.java)
        private val entityName = EntityType.TYPE.entityName
    }

    override fun getEntityName(): String = entityName

    override fun import(results: ImportResults): ImportCounts {
        logger.info("Iniciando importação de $entityName...")
        val dtos: List<TypeDto> = jsonLoader.loadJson(JsonFile.TYPES.filePath)
        val counts = importData(dtos, typeRepository, { dto ->
            TypeEntity(id = dto.id, name = dto.name, color = dto.color)
        }, logger, entityName)
        results.add(entityName, counts)
        return counts
    }
}
